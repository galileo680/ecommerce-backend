package com.bartek.ecommerce.mapper;

import com.bartek.ecommerce.dto.CategoryDto;
import com.bartek.ecommerce.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();

        return categoryDto;
    }
}
