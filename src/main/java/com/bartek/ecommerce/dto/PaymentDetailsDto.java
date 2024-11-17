package com.bartek.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaymentDetailsDto {
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;


    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{13,19}", message = "Invalid card number")
    private String cardNumber;

    @NotBlank(message = "Cardholder name is required")
    private String cardHolderName;

    @NotBlank(message = "Card expiration date is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Invalid expiration date format (MM/YY)")
    private String cardExpirationDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "\\d{3,4}", message = "Invalid CVV")
    private String cvv;
}
