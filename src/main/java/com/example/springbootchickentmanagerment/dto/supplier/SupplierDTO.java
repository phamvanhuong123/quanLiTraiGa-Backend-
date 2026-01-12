package com.example.springbootchickentmanagerment.dto.supplier;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupplierDTO {
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 255, message = "Tên phải có độ dài từ 2 đến 255 ký tự")
    private String name;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
     regexp = "^0\\d{9}$", message = "Số điện thoại không hợp lệ"
    )
    private String phone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
