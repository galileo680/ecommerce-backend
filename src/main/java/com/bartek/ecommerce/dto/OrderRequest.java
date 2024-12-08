package com.bartek.ecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {

    @NotNull(message = "Shipping address is required")
    @Valid
    private AddressDto shippingAddress;

    @NotNull(message = "Payment details are required")
    @Valid
    private PaymentDetailsDto paymentDetails;

    private String orderNotes;

    private String couponCode;
}