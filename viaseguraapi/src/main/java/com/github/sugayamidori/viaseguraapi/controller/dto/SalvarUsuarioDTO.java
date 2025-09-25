package com.github.sugayamidori.viaseguraapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SalvarUsuarioDTO(
        @Email(message = "inválido")
        @NotBlank(message = "Campo obrigatório")
        String email,
        @NotBlank(message = "Campo obrigatório")
        String senha
) {
}
