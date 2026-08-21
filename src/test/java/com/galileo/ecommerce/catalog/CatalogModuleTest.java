package com.galileo.ecommerce.catalog;

import com.galileo.ecommerce.TestcontainersConfiguration;
import com.galileo.ecommerce.catalog.application.CategoryService;
import com.galileo.ecommerce.catalog.application.ProductAdminService;
import com.galileo.ecommerce.catalog.application.ProductCommands.CreateProduct;
import com.galileo.ecommerce.catalog.application.ProductQueryService;
import com.galileo.ecommerce.catalog.application.ProductSearchCriteria;
import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.common.domain.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@ApplicationModuleTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class CatalogModuleTest {

    @Autowired
    private ProductAdminService admin;

    @Autowired
    private ProductQueryService queries;

    @Autowired
    private CategoryService categories;

    @Test
    void createdProductBecomesSearchableAfterActivation() {
        UUID categoryId = categories.create("Module test %s".formatted(UUID.randomUUID()), null);
        UUID productId = admin.create(new CreateProduct("MOD-%s".formatted(unique()), "Module product",
            null, Money.of(new BigDecimal("10.00"), "PLN"), categoryId, Map.of()));

        ProductSearchCriteria inCategory = new ProductSearchCriteria(categoryId, null, null, null);
        assertThat(queries.search(inCategory, PageRequest.of(0, 10))).isEmpty();

        admin.activate(productId);

        assertThat(queries.search(inCategory, PageRequest.of(0, 10)))
            .singleElement()
            .satisfies(product -> assertThat(product.getId()).isEqualTo(productId));
    }

    @Test
    void duplicateSkuIsRejected() {
        UUID categoryId = categories.create("Module test %s".formatted(UUID.randomUUID()), null);
        String sku = "DUP-%s".formatted(unique());
        admin.create(new CreateProduct(sku, "First", null, null, categoryId, Map.of()));

        assertThatExceptionOfType(BusinessRuleException.class)
            .isThrownBy(() -> admin.create(new CreateProduct(sku, "Second", null, null, categoryId, Map.of())));
    }

    private String unique() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
