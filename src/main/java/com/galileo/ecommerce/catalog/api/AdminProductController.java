package com.galileo.ecommerce.catalog.api;

import com.galileo.ecommerce.catalog.api.ProductDtos.CreateProductRequest;
import com.galileo.ecommerce.catalog.api.ProductDtos.UpdateProductRequest;
import com.galileo.ecommerce.catalog.application.ProductAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/products")
@Tag(name = "Products admin", description = "Product management")
@RequiredArgsConstructor
class AdminProductController {

    private final ProductAdminService service;
    private final CatalogMapper mapper;

    @PostMapping
    @Operation(summary = "Create a product in draft status")
    ResponseEntity<Void> create(@Valid @RequestBody CreateProductRequest request) {
        UUID id = service.create(mapper.toCommand(request));
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path("/api/v1/products/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product")
    ResponseEntity<Void> update(@PathVariable UUID id, @Valid @RequestBody UpdateProductRequest request) {
        service.update(id, mapper.toCommand(request));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate a product so it becomes publicly visible")
    ResponseEntity<Void> activate(@PathVariable UUID id) {
        service.activate(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Archive a product, it stays in the database but leaves the API")
    ResponseEntity<Void> archive(@PathVariable UUID id) {
        service.archive(id);
        return ResponseEntity.noContent().build();
    }
}
