package com.example.springbootchickentmanagerment.dto.breed;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BreedDTO {
    private Long id;

    @NotBlank(message = "Tên giống không được để trống")
    @Size(max = 100, message = "Tên giống không được vượt quá 100 ký tự")
    private String name;

    @NotNull(message = "Cân nặng mục tiêu không được để trống")
    @Min(value = 0, message = "Cân nặng mục tiêu không được là số âm")
    private Double targetWeight;

    @NotNull(message = "Số ngày trưởng thành không được để trống")
    @Min(value = 1, message = "Số ngày trưởng thành phải lớn hơn 0")
    private Integer maturityDays;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
