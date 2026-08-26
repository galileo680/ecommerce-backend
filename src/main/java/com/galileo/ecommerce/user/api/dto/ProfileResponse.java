package com.galileo.ecommerce.user.api.dto;

import com.galileo.ecommerce.user.domain.UserRole;

import java.util.UUID;

public record ProfileResponse(UUID id, String email, String firstName, String lastName, UserRole role) {
}
