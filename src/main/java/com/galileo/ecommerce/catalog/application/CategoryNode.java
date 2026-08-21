package com.galileo.ecommerce.catalog.application;

import java.util.List;
import java.util.UUID;

public record CategoryNode(UUID id, String name, String slug, List<CategoryNode> children) {
}
