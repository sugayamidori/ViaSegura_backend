package com.github.sugayamidori.viaseguraapi.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PredictionDTO(
        String h3Cell,
        LocalDate weekStart,
        BigDecimal predictedAccidents,
        LocalDateTime createdAt
) {
}
