package com.galileo.ecommerce.catalog.application;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSearchCriteria(UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice, String phrase) {
}
