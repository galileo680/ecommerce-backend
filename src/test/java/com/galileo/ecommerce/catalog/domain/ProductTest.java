package com.galileo.ecommerce.catalog.domain;

import com.galileo.ecommerce.common.domain.BusinessRuleException;
import com.galileo.ecommerce.common.domain.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class ProductTest {

    private static final UUID CATEGORY = UUID.randomUUID();
    private static final Money PRICE = Money.of(new BigDecimal("99.99"), "PLN");

    @Test
    void newProductStartsAsDraft() {
        Product product = draftWithPrice();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
        assertThat(product.getId()).isNotNull();
    }

    @Test
    void activationRequiresPrice() {
        Product product = Product.create(new Sku("ABC-123"), "Widget", null, null, CATEGORY, Map.of());

        assertThatExceptionOfType(BusinessRuleException.class).isThrownBy(product::activate);

        product.update("Widget", null, PRICE, CATEGORY, Map.of());
        product.activate();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void activationOfActiveProductIsRejected() {
        Product product = draftWithPrice();
        product.activate();

        assertThatExceptionOfType(BusinessRuleException.class).isThrownBy(product::activate);
    }

    @Test
    void activeProductCannotLoseItsPrice() {
        Product product = draftWithPrice();
        product.activate();

        assertThatExceptionOfType(BusinessRuleException.class)
            .isThrownBy(() -> product.update("Widget", null, null, CATEGORY, Map.of()));
    }

    @Test
    void archivedProductRejectsChanges() {
        Product product = draftWithPrice();
        product.archive();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ARCHIVED);
        assertThatExceptionOfType(BusinessRuleException.class).isThrownBy(product::activate);
        assertThatExceptionOfType(BusinessRuleException.class)
            .isThrownBy(() -> product.update("Widget", null, PRICE, CATEGORY, Map.of()));
    }

    @Test
    void negativePriceIsRejected() {
        Product product = draftWithPrice();

        assertThatExceptionOfType(BusinessRuleException.class)
            .isThrownBy(() -> product.update("Widget", null, Money.of(new BigDecimal("-1"), "PLN"), CATEGORY, Map.of()));
    }

    @Test
    void skuIsNormalizedAndValidated() {
        assertThat(new Sku(" abc-123 ").value()).isEqualTo("ABC-123");
        assertThatIllegalArgumentException().isThrownBy(() -> new Sku("ab"));
        assertThatIllegalArgumentException().isThrownBy(() -> new Sku("bad sku"));
        assertThatIllegalArgumentException().isThrownBy(() -> new Sku(null));
    }

    private Product draftWithPrice() {
        return Product.create(new Sku("ABC-123"), "Widget", "A widget", PRICE, CATEGORY, Map.of("color", "red"));
    }
}
