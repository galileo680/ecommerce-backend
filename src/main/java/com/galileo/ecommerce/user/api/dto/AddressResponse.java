package com.galileo.ecommerce.user.api.dto;

import java.util.UUID;

public record AddressResponse(UUID id, String label, String street, String city, String postalCode,
                              String country, boolean defaultAddress) {
}
