package com.galileo.ecommerce.catalog.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record MoneyDto(
    @NotNull @DecimalMin("0.00") BigDecimal amount,
    @NotNull @Pattern(regexp = "[A-Z]{3}") String currency) {
}
