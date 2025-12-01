package com.github.sugayamidori.viaseguraapi.controller.common;

import com.github.sugayamidori.viaseguraapi.controller.dto.FieldError;
import com.github.sugayamidori.viaseguraapi.controller.dto.ErrorResponse;
import com.github.sugayamidori.viaseguraapi.exceptions.InvalidFieldException;
import com.github.sugayamidori.viaseguraapi.exceptions.NotPermittedOperationException;
import com.github.sugayamidori.viaseguraapi.exceptions.DuplicatedRegistryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation error: {}", e.getMessage());
        List<org.springframework.validation.FieldError> fieldErrors = e.getFieldErrors();
        List<FieldError> listaErros = fieldErrors
                .stream()
                .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return new ErrorResponse((HttpStatus.UNPROCESSABLE_ENTITY.value()), "Validation error", listaErros);
    }

    @ExceptionHandler(DuplicatedRegistryException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicatedRegistryException(DuplicatedRegistryException e) {
        return ErrorResponse.conflito(e.getMessage());
    }

    @ExceptionHandler(NotPermittedOperationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleNotPermittedOperationException(NotPermittedOperationException e) {
        return ErrorResponse.respostaPadrao(e.getMessage());
    }

    @ExceptionHandler(InvalidFieldException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleInvalidFieldException(InvalidFieldException e) {
        return new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error",
                List.of(new FieldError(e.getField(), e.getMessage())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access Denied.", List.of());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedErrors(RuntimeException e) {
        log.error("Unexpected Error", e);
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please contact the administration.",
                List.of());
    }
}
