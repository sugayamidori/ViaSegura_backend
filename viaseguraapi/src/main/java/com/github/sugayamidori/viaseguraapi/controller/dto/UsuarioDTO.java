package com.github.sugayamidori.viaseguraapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UsuarioDTO(
        @NotBlank(message = "Campo obrigatório")
        String nome,
        @Email(message = "inválido")
        @NotBlank(message = "Campo obrigatório")
        String email,
        @NotBlank(message = "Campo obrigatório")
        String senha,
        List<String> roles
) {
}
