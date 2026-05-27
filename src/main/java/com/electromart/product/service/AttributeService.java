package com.electromart.product.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.electromart.product.Attribute;
import com.electromart.product.Category;
import com.electromart.product.dto.AttributeRequestDto;
import com.electromart.product.dto.AttributeResponseDto;
import com.electromart.product.repository.AttributeRepository;
import com.electromart.product.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AttributeService {

    private final AttributeRepository attributeRepository;
    private final CategoryRepository categoryRepository;

    // 🌍 Public
    public List<AttributeResponseDto> getByCategory(Long categoryId) {
        return attributeRepository.findByCategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    

    // 🔐 Admin
    public AttributeResponseDto create(AttributeRequestDto dto) {

        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Attribute attribute = Attribute.builder()
                .name(dto.getName())
                .dataType(dto.getDataType())
                .unit(dto.getUnit())
                .filterable(dto.getFilterable())
                .active(true)
                .category(category)
                .build();

        return mapToDto(attributeRepository.save(attribute));
    }

    public void changeStatus(Long id, Boolean active) {
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attribute not found"));
        attribute.setActive(active);
        attributeRepository.save(attribute);
    }

    public void changeFilterable(Long id, Boolean filterable) {
    Attribute attr = attributeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attribute not found"));
    attr.setFilterable(filterable);
    attributeRepository.save(attr);
   }

   public AttributeResponseDto update(Long id, AttributeRequestDto dto) {

    Attribute attr = attributeRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Attribute not found"));

    attr.setName(dto.getName());
    attr.setDataType(dto.getDataType());
    attr.setUnit(dto.getUnit());
    attr.setFilterable(dto.getFilterable());

    return mapToDto(attributeRepository.save(attr));
}



    private AttributeResponseDto mapToDto(Attribute attribute) {
        return AttributeResponseDto.builder()
                .id(attribute.getId())
                .name(attribute.getName())
                .dataType(attribute.getDataType())
                .unit(attribute.getUnit())
                .filterable(attribute.isFilterable())
                .active(attribute.getActive())
                .categoryId(attribute.getCategory().getId())
                .build();
    }
}
