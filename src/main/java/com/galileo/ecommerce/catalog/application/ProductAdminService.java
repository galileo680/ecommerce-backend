package com.galileo.ecommerce.catalog.application;

import com.galileo.ecommerce.catalog.application.ProductCommands.CreateProduct;
import com.galileo.ecommerce.catalog.application.ProductCommands.UpdateProduct;
import com.galileo.ecommerce.catalog.domain.Product;
import com.galileo.ecommerce.catalog.domain.Sku;
import com.galileo.ecommerce.catalog.infrastructure.CategoryRepository;
import com.galileo.ecommerce.catalog.infrastructure.ProductRepository;
import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.common.domain.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductAdminService {

    private final ProductRepository products;
    private final CategoryRepository categories;

    public UUID create(CreateProduct command) {
        Sku sku = new Sku(command.sku());
        if (products.existsBySku(sku.value())) {
            throw new BusinessRuleException("sku %s is already used".formatted(sku.value()));
        }
        requireCategory(command.categoryId());
        Product product = Product.create(sku, command.name(), command.description(), command.price(),
            command.categoryId(), command.attributes());
        return products.save(product).getId();
    }

    public void update(UUID id, UpdateProduct command) {
        requireCategory(command.categoryId());
        Product product = requireProduct(id);
        product.update(command.name(), command.description(), command.price(), command.categoryId(),
            command.attributes());
    }

    public void activate(UUID id) {
        requireProduct(id).activate();
    }

    public void archive(UUID id) {
        requireProduct(id).archive();
    }

    private Product requireProduct(UUID id) {
        return products.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("product %s not found".formatted(id)));
    }

    private void requireCategory(UUID categoryId) {
        if (!categories.existsById(categoryId)) {
            throw new ResourceNotFoundException("category %s not found".formatted(categoryId));
        }
    }
}
