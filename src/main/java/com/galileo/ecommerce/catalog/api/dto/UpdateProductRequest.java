package com.galileo.ecommerce.catalog.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record UpdateProductRequest(
    @NotBlank @Size(max = 255) String name,
    @Size(max = 4000) String description,
    @Valid MoneyDto price,
    @NotNull UUID categoryId,
    Map<String, String> attributes) {
}
