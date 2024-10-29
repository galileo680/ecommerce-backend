package com.bartek.ecommerce.service;

import com.bartek.ecommerce.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto categoryRequest);
    CategoryDto updateCategory(Long categoryId, CategoryDto categoryRequest);
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Long categoryId);
    void deleteCategory(Long categoryId);
}
