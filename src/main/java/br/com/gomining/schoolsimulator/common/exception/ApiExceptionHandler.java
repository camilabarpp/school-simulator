package br.com.gomining.schoolsimulator.common.exception;

import br.com.gomining.schoolsimulator.common.exception.errorresponse.ErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ApiExceptionHandler extends DefaultResponseErrorHandler {

    @ExceptionHandler(ApiNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse apiNotFoundExceptionHandler(ApiNotFoundException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .field(NOT_FOUND.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(ActivityAlreadyAddedException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse activityAlreadyAddedException(ActivityAlreadyAddedException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .field(BAD_REQUEST.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(TokenGenerationException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse tokenGenerationExceptionHandler(TokenGenerationException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .field(INTERNAL_SERVER_ERROR.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ErrorResponse accessDeniedExceptionHandler(AccessDeniedException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .field(FORBIDDEN.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse tokenInvalidExceptionHandler(InvalidTokenException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .field(UNAUTHORIZED.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage())
                .field(BAD_REQUEST.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ErrorResponse authenticationServiceExceptionHandler(AuthenticationServiceException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .field(UNAUTHORIZED.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse webExchangeBindExceptionHandler(WebExchangeBindException ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage())
                .field(BAD_REQUEST.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ErrorResponse runtimeExceptionHandler(Exception ex) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .message(ex.getMessage())
                .field(INTERNAL_SERVER_ERROR.name())
                .parameter(ex.getClass().getSimpleName())
                .build();
    }
}
