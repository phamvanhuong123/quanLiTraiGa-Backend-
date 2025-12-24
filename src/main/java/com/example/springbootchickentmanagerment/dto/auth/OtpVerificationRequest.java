package com.example.springbootchickentmanagerment.dto.auth;

import lombok.Data;

@Data
public class OtpVerificationRequest {
    private String email;
    private String code;
}
