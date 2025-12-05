package com.github.sugayamidori.viaseguraapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Field with error")
public record FieldError(
        @Schema(description = "Field name")
        String field,
        @Schema(description = "Occurred error")
        String error) {
}
