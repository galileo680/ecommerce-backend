package com.bartek.ecommerce.service.Impl;

import com.bartek.ecommerce.dto.CategoryDto;
import com.bartek.ecommerce.entity.Category;
import com.bartek.ecommerce.exception.NotFoundException;
import com.bartek.ecommerce.mapper.CategoryMapper;
import com.bartek.ecommerce.repository.CategoryRepository;
import com.bartek.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(CategoryDto categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        categoryRepository.save(category);

        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);

        return categoryDto;
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryRequest) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new NotFoundException("Category Not Found"));
        category.setName(categoryRequest.getName());
        categoryRepository.save(category);

        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);

        return categoryDto;
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        List<CategoryDto> categoryDtoList = categories.stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());

        return categoryDtoList;
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new NotFoundException("Category not Found"));
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);

        return categoryDto;
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(()-> new NotFoundException("Category not Found"));

        categoryRepository.delete(category);
    }
}
