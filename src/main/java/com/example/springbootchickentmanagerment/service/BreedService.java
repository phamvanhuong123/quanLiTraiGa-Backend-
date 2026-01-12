package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.inventory.BreedDTO;
import com.example.springbootchickentmanagerment.entity.Breed;
import com.example.springbootchickentmanagerment.repository.BreedRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return breedRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public BreedDTO getBreedById(Long id) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found with id: " + id));
        return mapToDTO(breed);
    }

    public BreedDTO updateBreed(Long id, BreedDTO breedDTO) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found with id: " + id));
        
        breed.setName(breedDTO.getName());
        breed.setTargetWeight(breedDTO.getTargetWeight());
        breed.setMaturityDays(breedDTO.getMaturityDays());
        
        Breed updatedBreed = breedRepository.save(breed);
        return mapToDTO(updatedBreed);
    }

    public void deleteBreed(Long id) {
        Breed breed = breedRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Breed not found with id: " + id));
        breedRepository.delete(breed);
    }

    private Breed mapToEntity(BreedDTO dto) {
        return Breed.builder()
                .name(dto.getName())
                .targetWeight(dto.getTargetWeight())
                .maturityDays(dto.getMaturityDays())
                .build();
    }

    private BreedDTO mapToDTO(Breed entity) {
        return BreedDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .targetWeight(entity.getTargetWeight())
                .maturityDays(entity.getMaturityDays())
                .build();
    }
}