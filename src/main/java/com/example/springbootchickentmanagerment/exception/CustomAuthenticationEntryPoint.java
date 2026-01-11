package com.example.springbootchickentmanagerment.exception;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Create the custom response
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .message("Thất bại bạn chưa gửi token")
                .build();

        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        // Write the response as JSON
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}
