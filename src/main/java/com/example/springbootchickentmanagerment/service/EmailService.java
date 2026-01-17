package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOtpEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Your One-Time Password (OTP) for Account Verification");
            message.setText("Cảm ơn đã đăng kí. \n\n"
                    + "Mã OTP của bạn là: " + otp + "\n\n"
                    + "Mã xác thực này có thời han là 10 phút. Vui lòng không gửi cho bất kì ai");
            mailSender.send(message);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while sending OTP email: " + e.getMessage());
        }
    }
}
