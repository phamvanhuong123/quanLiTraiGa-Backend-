package com.example.springbootchickentmanagerment.dto.Schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateScheduleDTO {

    @NotNull(message = "Flock ID không được để trống")
    private Long flockId;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không quá 200 ký tự")
    private String title;

    @Size(max = 500, message = "Mô tả không quá 500 ký tự")
    private String description;

    @NotNull(message = "Ngày thực hiện không được để trống")
    private LocalDate scheduledDate; 
}