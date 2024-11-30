package com.bartek.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    private String firstname;

    @NotBlank(message = "Last name is required")
    private String lastname;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password should be at least 6 characters")
    @NotBlank(message = "Password is required")
    private String password;

    private String phoneNumber;
}
