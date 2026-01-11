package com.example.springbootchickentmanagerment.dto.inventory;

import com.example.springbootchickentmanagerment.enums.MaterialType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MaterialDTO {
    private Long id;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải có độ dài từ 2 đến 100 ký tự")
    private String name;

    @NotBlank(message = "Đơn vị không được để trống")
    private String unit;

    @NotNull(message = "Material type cannot be null")
    private MaterialType type;
}
