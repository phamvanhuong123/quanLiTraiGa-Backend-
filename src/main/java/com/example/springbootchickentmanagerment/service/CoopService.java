package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.inventory.CoopDTO;
import com.example.springbootchickentmanagerment.entity.Coop;
import com.example.springbootchickentmanagerment.enums.CoopStatus;
import com.example.springbootchickentmanagerment.repository.CoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoopService {

    @Autowired
    private CoopRepository coopRepository;

    public CoopDTO createCoop(CoopDTO coopDTO) {
        Coop coop = mapToEntity(coopDTO);
        coop.setStatus(CoopStatus.EMPTY);
        Coop savedCoop = coopRepository.save(coop);
        return mapToDTO(savedCoop);
    }

    public List<CoopDTO> getAllCoops() {
        return coopRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CoopDTO getCoopById(Long id) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coop not found with id: " + id));
        return mapToDTO(coop);
    }

    public List<CoopDTO> getEmptyCoops() {
        return coopRepository.findByStatus(CoopStatus.EMPTY).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CoopDTO updateCoop(Long id, CoopDTO coopDTO) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coop not found with id: " + id));
        
        coop.setName(coopDTO.getName());
        coop.setCapacity(coopDTO.getCapacity());
        // Status should not be updated manually
        
        Coop updatedCoop = coopRepository.save(coop);
        return mapToDTO(updatedCoop);
    }

    public void deleteCoop(Long id) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coop not found with id: " + id));
        coopRepository.delete(coop);
    }

    private Coop mapToEntity(CoopDTO dto) {
        return Coop.builder()
                .name(dto.getName())
                .capacity(dto.getCapacity())
                .build();
    }

    private CoopDTO mapToDTO(Coop entity) {
        return CoopDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .capacity(entity.getCapacity())
                .status(entity.getStatus().name())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}