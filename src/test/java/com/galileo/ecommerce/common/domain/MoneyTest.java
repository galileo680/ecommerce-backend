package com.galileo.ecommerce.common.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class MoneyTest {

    @Test
    void normalizesScaleToTwoDigits() {
        assertThat(Money.of(new BigDecimal("10"), "PLN")).isEqualTo(Money.of(new BigDecimal("10.00"), "PLN"));
        assertThat(Money.of(new BigDecimal("10.005"), "PLN").amount()).isEqualTo(new BigDecimal("10.01"));
    }

    @Test
    void addsAmountsOfTheSameCurrency() {
        Money sum = Money.of(new BigDecimal("10.50"), "PLN").add(Money.of(new BigDecimal("2.25"), "PLN"));

        assertThat(sum).isEqualTo(Money.of(new BigDecimal("12.75"), "PLN"));
    }

    @Test
    void refusesToAddDifferentCurrencies() {
        Money pln = Money.of(BigDecimal.ONE, "PLN");
        Money eur = Money.of(BigDecimal.ONE, "EUR");

        assertThatIllegalArgumentException().isThrownBy(() -> pln.add(eur));
    }

    @Test
    void multipliesByQuantity() {
        assertThat(Money.of(new BigDecimal("19.99"), "PLN").multiply(3))
            .isEqualTo(Money.of(new BigDecimal("59.97"), "PLN"));
    }

    @Test
    void rejectsMissingAmountOrInvalidCurrency() {
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of(null, "PLN"));
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of(BigDecimal.ONE, "zl"));
        assertThatIllegalArgumentException().isThrownBy(() -> Money.of(BigDecimal.ONE, null));
    }
}
