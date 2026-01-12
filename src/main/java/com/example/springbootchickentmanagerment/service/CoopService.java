package com.example.springbootchickentmanagerment.service;

/*
import com.example.springbootchickentmanagerment.dto.inventory.CoopDTO;
import com.example.springbootchickentmanagerment.entity.Coop;
import com.example.springbootchickentmanagerment.enums.CoopStatus;
import com.example.springbootchickentmanagerment.repository.CoopRepository;
import org.springframework.beans.factory.annotation.Autowired;
*/


import com.example.springbootchickentmanagerment.dto.coop.CoopDTO; // Corrected import
import com.example.springbootchickentmanagerment.entity.Coop;
import com.example.springbootchickentmanagerment.enums.CoopStatus;
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
        coop.setStatus(CoopStatus.EMPTY);
        Coop savedCoop = coopRepository.save(coop);
        return mapToDTO(savedCoop);
    }

    public List<CoopDTO> getAllCoops() {
        return coopRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /*
    public CoopDTO getCoopById(Long id) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coop not found with id: " + id));
        return mapToDTO(coop);
    }
    */
    
    public CoopDTO getCoopById(Long id) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy chuồng với id: " + id));
        return mapToDTO(coop);
    }
  
    public List<CoopDTO> getEmptyCoops() {
        return coopRepository.findByStatus(CoopStatus.EMPTY).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

   /*
    public CoopDTO updateCoop(Long id, CoopDTO coopDTO) {
        Coop coop = coopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coop not found with id: " + id));
        
        coop.setName(coopDTO.getName());
        coop.setCapacity(coopDTO.getCapacity());
        // Status should not be updated manually
        
        Coop updatedCoop = coopRepository.save(coop);
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tìm thấy chuồng với id: " + id));
        return mapToDTO(coop);
    }
   */

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
        return CoopDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .capacity(entity.getCapacity())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
