package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.auth.LoginRequest;
import com.example.springbootchickentmanagerment.dto.auth.OtpVerificationRequest;
import com.example.springbootchickentmanagerment.dto.auth.RegisterRequest;
import com.example.springbootchickentmanagerment.entity.User;
import com.example.springbootchickentmanagerment.entity.VerificationToken;
import com.example.springbootchickentmanagerment.enums.Role;
import com.example.springbootchickentmanagerment.enums.UserStatus;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.UserRepository;
import com.example.springbootchickentmanagerment.repository.VerificationTokenRepository;
import com.example.springbootchickentmanagerment.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VerificationTokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EmailService emailService;

    @Transactional
    public void register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Error: Email is already in use!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFullName(registerRequest.getUsername());
        user.setRole(Role.STAFF);

        User savedUser = userRepository.save(user);

        String otp = generateOtp();
        VerificationToken verificationToken = new VerificationToken(otp, savedUser);
        verificationToken.setExpiryDate(Instant.now().plus(10, ChronoUnit.MINUTES));
        tokenRepository.save(verificationToken);

        emailService.sendOtpEmail(savedUser.getEmail(), otp);
    }

    public String login(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new CustomException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new CustomException(HttpStatus.FORBIDDEN, "Account is not activated. Please check your email for verification code.");
            }

            // Corrected: Pass the whole user object to generate a token with full claims
            return jwtUtils.generateToken(user);

        } catch (AuthenticationException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
    }

    @Transactional
    public void verifyOtp(OtpVerificationRequest otpRequest) {
        User user = userRepository.findByEmail(otpRequest.getEmail())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "User not found with this email."));

        VerificationToken verificationToken = tokenRepository.findByUser(user)
                .orElseThrow(() -> new CustomException(HttpStatus.BAD_REQUEST, "Invalid or expired OTP. Please request a new one."));

        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            tokenRepository.delete(verificationToken);
            throw new CustomException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one.");
        }

        if (!verificationToken.getToken().equals(otpRequest.getCode())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "The OTP entered is incorrect.");
        }

        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);
    }

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(1000000);
        return String.format("%06d", num);
    }
}
