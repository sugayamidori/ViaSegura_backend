package com.github.sugayamidori.viaseguraapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UsuarioDTO(
        @NotBlank(message = "Campo obrigatório")
        String nome,
        @Email(message = "Insira um email válido")
        @NotBlank(message = "Campo obrigatório")
        String email,
        @NotEmpty(message = "Campo obrigatório")
        List<String> roles
) {
}
