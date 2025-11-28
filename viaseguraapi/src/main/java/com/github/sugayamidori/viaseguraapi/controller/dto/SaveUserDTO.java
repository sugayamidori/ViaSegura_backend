package com.github.sugayamidori.viaseguraapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SaveUserDTO(
        @NotBlank(message = "Required field")
        String name,
        @NotBlank(message = "Required field")
        String password,
        @Email(message = "Invalid email format")
        @NotBlank(message = "Required field")
        String email) {
}
