package com.github.sugayamidori.viaseguraapi.controller.dto;

import java.util.Date;

public record TokenDTO(String username, Boolean authenticated, Date created, Date expiration, String accessToken, String refreshToken) {
}
