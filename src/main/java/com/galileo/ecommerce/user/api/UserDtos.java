package com.galileo.ecommerce.user.api;

import com.galileo.ecommerce.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public final class UserDtos {

    private UserDtos() {
    }

    public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,72}$",
            message = "password must have at least 8 characters with an upper case letter, a lower case letter and a digit")
        String password,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName) {
    }

    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }

    public record RefreshRequest(@NotBlank String refreshToken) {
    }

    public record TokenResponse(String accessToken, String refreshToken) {
    }

    public record IdResponse(UUID id) {
    }

    public record ProfileResponse(UUID id, String email, String firstName, String lastName, UserRole role) {
    }

    public record UpdateProfileRequest(
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName) {
    }

    public record AddressRequest(
        @NotBlank @Size(max = 100) String label,
        @NotBlank @Size(max = 255) String street,
        @NotBlank @Size(max = 100) String city,
        @NotBlank @Size(max = 20) String postalCode,
        @NotBlank @Size(max = 100) String country) {
    }

    public record AddressResponse(UUID id, String label, String street, String city, String postalCode,
                                  String country, boolean defaultAddress) {
    }

    public record AddressListResponse(List<AddressResponse> addresses) {
    }
}
