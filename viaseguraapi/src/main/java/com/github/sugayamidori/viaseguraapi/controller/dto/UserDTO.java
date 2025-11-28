package com.github.sugayamidori.viaseguraapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UserDTO(
        @NotBlank(message = "Required field")
        String name,
        @Email(message = "Invalid email format")
        @NotBlank(message = "Required field")
        String email,
        @NotEmpty(message = "Required field")
        List<String> roles
) {
}
