package com.galileo.ecommerce.catalog.application;

import com.galileo.ecommerce.common.domain.Money;

import java.util.Map;
import java.util.UUID;

public final class ProductCommands {

    private ProductCommands() {
    }

    public record CreateProduct(String sku, String name, String description, Money price, UUID categoryId,
                                Map<String, String> attributes) {
    }

    public record UpdateProduct(String name, String description, Money price, UUID categoryId,
                                Map<String, String> attributes) {
    }
}
