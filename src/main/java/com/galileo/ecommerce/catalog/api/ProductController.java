package com.galileo.ecommerce.catalog.api;

import com.galileo.ecommerce.catalog.api.dto.ProductResponse;
import com.galileo.ecommerce.catalog.api.dto.PageResponse;
import com.galileo.ecommerce.catalog.application.ProductQueryService;
import com.galileo.ecommerce.catalog.application.ProductSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Public product catalog")
@RequiredArgsConstructor
class ProductController {

    private static final int MAX_PAGE_SIZE = 50;
    private static final Map<String, String> SORTABLE = Map.of(
        "name", "name",
        "price", "priceAmount",
        "createdAt", "createdAt");

    private final ProductQueryService queries;
    private final CatalogMapper mapper;

    @GetMapping
    @Operation(summary = "List active products with paging, sorting and filters")
    PageResponse<ProductResponse> search(
        @RequestParam(required = false) UUID category,
        @RequestParam(required = false) BigDecimal minPrice,
        @RequestParam(required = false) BigDecimal maxPrice,
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createdAt,desc") String sort) {
        ProductSearchCriteria criteria = new ProductSearchCriteria(category, minPrice, maxPrice, search);
        Pageable pageable = PageRequest.of(Math.max(page, 0), clampSize(size), parseSort(sort));
        return PageResponse.of(queries.search(criteria, pageable).map(mapper::toResponse));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one active product")
    ProductResponse get(@PathVariable UUID id) {
        return mapper.toResponse(queries.getActive(id));
    }

    private int clampSize(int size) {
        return Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
    }

    private Sort parseSort(String sort) {
        String[] parts = sort.split(",");
        String property = SORTABLE.get(parts[0].trim());
        if (property == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "sort must be one of %s".formatted(SORTABLE.keySet()));
        }
        Sort.Direction direction = parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        return Sort.by(direction, property);
    }
}
