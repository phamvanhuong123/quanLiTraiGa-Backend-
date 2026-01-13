package com.example.springbootchickentmanagerment.dto.coop;

import com.example.springbootchickentmanagerment.enums.CoopStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CoopDTO {
    private Long id;

    @NotBlank(message = "Tên chuồng không được để trống")
    @Size(max = 100, message = "Tên chuồng không được vượt quá 100 ký tự")
    private String name;

    @NotNull(message = "Sức chứa không được để trống")
    @Min(value = 1, message = "Sức chứa phải lớn hơn 0")
    private Integer capacity;

    private Integer currentQuantity;

    @NotNull(message = "Trạng thái không được để trống")
    private CoopStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
