package com.galileo.ecommerce.catalog.domain;

public record Sku(String value) {

    public Sku {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("sku must not be blank");
        }
        value = value.trim().toUpperCase();
        if (!value.matches("[A-Z0-9][A-Z0-9-]{2,63}")) {
            throw new IllegalArgumentException("sku must be 3 to 64 characters of letters, digits and dashes");
        }
    }
}
