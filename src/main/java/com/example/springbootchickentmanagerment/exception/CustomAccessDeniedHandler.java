package com.example.springbootchickentmanagerment.exception;

import com.example.springbootchickentmanagerment.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // Create the custom response
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .statusCode(HttpStatus.FORBIDDEN.value())
                .message("Access Denied. You do not have the required permissions to access this resource.")
                .build();

        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        // Write the response as JSON
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}
