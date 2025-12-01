package com.github.sugayamidori.viaseguraapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record H3CoordinatesDTO(
        String h3Cell,
        BigDecimal latitude,
        BigDecimal longitude,
        String neighborhood,
        LocalDateTime createdAt
) {
}
