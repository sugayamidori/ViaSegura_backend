package com.github.sugayamidori.viaseguraapi.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.util.List;

@Schema(description = "Model to error response")
public record ErrorResponse(
        @Schema(description = "HTTP status", example = "500")
        int status,
        @Schema(description = "Error message", example = "Unexpected error occurred")
        String mensagem,
        @Schema(description = "List of fields with errors", implementation = FieldError.class)
        List<FieldError> erros) {

    public static ErrorResponse respostaPadrao(String mensagem) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), mensagem, List.of());
    }

    public static ErrorResponse conflito(String mensagem) {
        return new ErrorResponse(HttpStatus.CONFLICT.value(), mensagem, List.of());
    }
}
