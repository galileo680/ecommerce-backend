package com.galileo.ecommerce.catalog.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public final class CategoryDtos {

    private CategoryDtos() {
    }

    public record CreateCategoryRequest(@NotBlank @Size(max = 255) String name, UUID parentId) {
    }

    public record UpdateCategoryRequest(@NotBlank @Size(max = 255) String name) {
    }

    public record CategoryResponse(UUID id, String name, String slug, List<CategoryResponse> children) {
    }
}
