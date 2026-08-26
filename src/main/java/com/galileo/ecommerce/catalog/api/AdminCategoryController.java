package com.galileo.ecommerce.catalog.api;

import com.galileo.ecommerce.catalog.api.dto.CreateCategoryRequest;
import com.galileo.ecommerce.catalog.api.dto.UpdateCategoryRequest;
import com.galileo.ecommerce.catalog.api.dto.IdResponse;
import com.galileo.ecommerce.catalog.application.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/categories")
@Tag(name = "Categories admin", description = "Category management")
@RequiredArgsConstructor
class AdminCategoryController {

    private final CategoryService service;

    @PostMapping
    @Operation(summary = "Create a category, optionally under a parent")
    ResponseEntity<IdResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        UUID id = service.create(request.name(), request.parentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new IdResponse(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename a category")
    ResponseEntity<Void> rename(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest request) {
        service.rename(id, request.name());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a category without subcategories and products")
    ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
