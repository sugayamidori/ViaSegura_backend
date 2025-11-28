package com.github.sugayamidori.viaseguraapi.controller.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorResponse(int status, String mensagem, List<FieldError> erros) {

    public static ErrorResponse respostaPadrao(String mensagem) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), mensagem, List.of());
    }

    public static ErrorResponse conflito(String mensagem) {
        return new ErrorResponse(HttpStatus.CONFLICT.value(), mensagem, List.of());
    }
}
