package com.galileo.ecommerce.catalog.api.dto;

import com.galileo.ecommerce.catalog.domain.ProductStatus;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

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
