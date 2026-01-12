package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.breed.BreedDTO;
import com.example.springbootchickentmanagerment.entity.Breed;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.BreedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BreedService {

    @Autowired
    private BreedRepository breedRepository;

    public BreedDTO createBreed(BreedDTO breedDTO) {
        Breed breed = mapToEntity(breedDTO);
        Breed savedBreed = breedRepository.save(breed);
        return mapToDTO(savedBreed);
    }

    public List<BreedDTO> getAllBreeds() {
        return breedRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BreedDTO getBreedById(Long id) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy giống gà với id: " + id));
        return mapToDTO(breed);
    }

    public BreedDTO updateBreed(Long id, BreedDTO breedDTO) {
        Breed existingBreed = breedRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy giống gà với id: " + id));

        existingBreed.setName(breedDTO.getName());
        existingBreed.setTargetWeight(breedDTO.getTargetWeight());
        existingBreed.setMaturityDays(breedDTO.getMaturityDays());

        Breed updatedBreed = breedRepository.save(existingBreed);
        return mapToDTO(updatedBreed);
    }

    public void deleteBreed(Long id) {
        if (!breedRepository.existsById(id)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy giống gà với id: " + id);
        }
        // Consider adding logic to check if the breed is in use before deleting
        breedRepository.deleteById(id);
    }

    private Breed mapToEntity(BreedDTO dto) {
        return Breed.builder()
                .name(dto.getName())
                .targetWeight(dto.getTargetWeight())
                .maturityDays(dto.getMaturityDays())
                .build();
    }

    private BreedDTO mapToDTO(Breed entity) {
        BreedDTO dto = new BreedDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setTargetWeight(entity.getTargetWeight());
        dto.setMaturityDays(entity.getMaturityDays());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
