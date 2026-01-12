package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.coop.CoopDTO; // Corrected import
import com.example.springbootchickentmanagerment.entity.Coop;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.CoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoopService {

    @Autowired
    private CoopRepository coopRepository;

    public CoopDTO createCoop(CoopDTO coopDTO) {
        Coop coop = mapToEntity(coopDTO);
        Coop savedCoop = coopRepository.save(coop);
        return mapToDTO(savedCoop);
    }

    public List<CoopDTO> getAllCoops() {
        return coopRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CoopDTO getCoopById(Long id) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy chuồng với id: " + id));
        return mapToDTO(coop);
    }

    public CoopDTO updateCoop(Long id, CoopDTO coopDTO) {
        Coop existingCoop = coopRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy chuồng với id: " + id));

        existingCoop.setName(coopDTO.getName());
        existingCoop.setCapacity(coopDTO.getCapacity());
        existingCoop.setStatus(coopDTO.getStatus());

        Coop updatedCoop = coopRepository.save(existingCoop);
        return mapToDTO(updatedCoop);
    }

    public void deleteCoop(Long id) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy chuồng với id: " + id));

        if (coop.getStatus() != com.example.springbootchickentmanagerment.enums.CoopStatus.EMPTY) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Không thể xóa chuồng đang có gà hoặc chưa được dọn dẹp.");
        }

        coopRepository.deleteById(id);
    }

    private Coop mapToEntity(CoopDTO dto) {
        return Coop.builder()
                .name(dto.getName())
                .capacity(dto.getCapacity())
                .status(dto.getStatus())
                .build();
    }

    private CoopDTO mapToDTO(Coop entity) {
        CoopDTO dto = new CoopDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCapacity(entity.getCapacity());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
