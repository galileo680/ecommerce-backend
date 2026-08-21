package com.galileo.ecommerce.catalog.api;

import com.galileo.ecommerce.catalog.domain.ProductStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public final class ProductDtos {

    private ProductDtos() {
    }

    public record CreateProductRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9][A-Za-z0-9-]{2,63}") String sku,
        @NotBlank @Size(max = 255) String name,
        @Size(max = 4000) String description,
        @Valid MoneyDto price,
        @NotNull UUID categoryId,
        Map<String, String> attributes) {
    }

    public record UpdateProductRequest(
        @NotBlank @Size(max = 255) String name,
        @Size(max = 4000) String description,
        @Valid MoneyDto price,
        @NotNull UUID categoryId,
        Map<String, String> attributes) {
    }

    public record ProductResponse(
        UUID id,
        String sku,
        String name,
        String description,
        MoneyDto price,
        UUID categoryId,
        Map<String, String> attributes,
        ProductStatus status,
        Instant createdAt,
        Instant updatedAt) {
    }

    public record IdResponse(UUID id) {
    }
}
