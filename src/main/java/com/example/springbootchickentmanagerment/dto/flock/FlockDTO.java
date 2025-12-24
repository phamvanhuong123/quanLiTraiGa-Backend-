package com.example.springbootchickentmanagerment.dto.flock;

import com.example.springbootchickentmanagerment.enums.FlockStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class FlockDTO {
    private Long id;
    private String name;
    private String batchCode;
    private String coopName;
    private String breedName;
    private Integer currentQuantity;
    private FlockStatus status;
    private LocalDate importDate;
}
