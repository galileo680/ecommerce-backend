package com.galileo.ecommerce.catalog.api;

import com.galileo.ecommerce.catalog.api.dto.CategoryResponse;
import com.galileo.ecommerce.catalog.application.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Public category tree")
@RequiredArgsConstructor
class CategoryController {

    private final CategoryService service;
    private final CatalogMapper mapper;

    @GetMapping
    @Operation(summary = "Get the whole category tree")
    List<CategoryResponse> tree() {
        return mapper.toCategoryResponses(service.tree());
    }
}
