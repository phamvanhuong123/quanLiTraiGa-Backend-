package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.inventory.MaterialDTO;
import com.example.springbootchickentmanagerment.entity.Material;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public MaterialDTO createMaterial(MaterialDTO materialDTO) {
        Material material = mapToEntity(materialDTO);
        Material savedMaterial = materialRepository.save(material);
        return mapToDTO(savedMaterial);
    }

    public List<MaterialDTO> getAllMaterials() {
        return materialRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public MaterialDTO getMaterialById(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Material not found with id: " + id));
        return mapToDTO(material);
    }

    public MaterialDTO updateMaterial(Long id, MaterialDTO materialDTO) {
        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy vật tư " + id));

        existingMaterial.setName(materialDTO.getName());
        existingMaterial.setUnit(materialDTO.getUnit());
        existingMaterial.setType(materialDTO.getType());

        Material updatedMaterial = materialRepository.save(existingMaterial);
        return mapToDTO(updatedMaterial);
    }

    public void deleteMaterial(Long id) {
        if (!materialRepository.existsById(id)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Material not found with id: " + id);
        }
        materialRepository.deleteById(id);
    }

    private Material mapToEntity(MaterialDTO dto) {
        return Material.builder()
                .name(dto.getName())
                .unit(dto.getUnit())
                .type(dto.getType())
                .build();
    }

    private MaterialDTO mapToDTO(Material entity) {
        MaterialDTO dto = new MaterialDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUnit(entity.getUnit());
        dto.setType(entity.getType());

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
