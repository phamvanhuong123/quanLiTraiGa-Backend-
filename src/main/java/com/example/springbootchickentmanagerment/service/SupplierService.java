package com.example.springbootchickentmanagerment.service;

import com.example.springbootchickentmanagerment.dto.inventory.MaterialDTO;
import com.example.springbootchickentmanagerment.dto.supplier.SupplierDTO;
import com.example.springbootchickentmanagerment.entity.Material;
import com.example.springbootchickentmanagerment.entity.Supplier;
import com.example.springbootchickentmanagerment.exception.CustomException;
import com.example.springbootchickentmanagerment.repository.SupplierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    // Lấy danh sach nha cung cap
    public List<SupplierDTO> getAllSupplier(){
        return supplierRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(this::mapDTO)
                .collect(Collectors.toList());
    }

    //Tao nha cung cap
    public SupplierDTO createSupplier(SupplierDTO supplierDTO){
        Supplier supplier = mapToEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        return mapDTO(savedSupplier);
    }
    //Cật nhập thông tin nhà cung cấp
    public  SupplierDTO updateSupplier(Long id,SupplierDTO supplierDTO){
        Supplier existSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND,"Không tìm thấy nhà cung cấp"));

        existSupplier.setName(supplierDTO.getName());
        existSupplier.setAddress(supplierDTO.getAddress());
        existSupplier.setPhone(supplierDTO.getPhone());
        Supplier updateSupplier = supplierRepository.save(existSupplier);
        return mapDTO(updateSupplier);
    }
    //Lấy thông tin 1 nhà cung cấp
    public SupplierDTO getSupplierById(Long id){
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, "Không tim thấy nhà cung cấp"));
        return mapDTO(supplier);
    }

    //Xoá nhà cung cấp
    public void deleteSupplier(Long id){
        if(!supplierRepository.existsById(id)){
            throw new CustomException(HttpStatus.NOT_FOUND,"Không tìm thấy nhà cung cấp");
        }
        supplierRepository.deleteById(id);
    }

    private SupplierDTO mapDTO(Supplier supplier){
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setAddress(supplier.getAddress());
        dto.setPhone(supplier.getPhone());
        dto.setUpdatedAt(supplier.getUpdatedAt());
        dto.setCreatedAt(supplier.getCreatedAt());
        return dto;
    }
    private Supplier mapToEntity(SupplierDTO dto) {
        return Supplier.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(dto.getPhone())
                .build();
    }
}
