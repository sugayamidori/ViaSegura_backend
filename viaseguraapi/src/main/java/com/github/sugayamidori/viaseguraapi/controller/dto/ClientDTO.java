package com.github.sugayamidori.viaseguraapi.controller.dto;

public record ClientDTO(
        String clientId,
        String clientSecret,
        String redirectURI,
        String scope
) {
}
