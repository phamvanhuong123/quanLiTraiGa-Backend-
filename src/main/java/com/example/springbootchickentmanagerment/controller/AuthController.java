package com.example.springbootchickentmanagerment.controller;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.example.springbootchickentmanagerment.dto.auth.AuthResponse;
import com.example.springbootchickentmanagerment.dto.auth.LoginRequest;
import com.example.springbootchickentmanagerment.dto.auth.OtpVerificationRequest;
import com.example.springbootchickentmanagerment.dto.auth.RegisterRequest;
import com.example.springbootchickentmanagerment.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody RegisterRequest registerRequest) {
        authService.register(registerRequest);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User registered successfully! Please check your email for the verification code.")
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest);
        AuthResponse authResponse = new AuthResponse(token);
        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(authResponse)
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@RequestBody OtpVerificationRequest otpRequest) {
        authService.verifyOtp(otpRequest);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account activated successfully!")
                .build();
        return ResponseEntity.ok(response);
    }
}
