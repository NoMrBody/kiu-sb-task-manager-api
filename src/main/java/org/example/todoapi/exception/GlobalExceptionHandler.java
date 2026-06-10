package org.example.todoapi.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<ApiException> handleApiRequestException(ApiRequestException e) {
        HttpStatus status = e.getStatus();
        String message = messageSource.getMessage(e.getMessageKey(), e.getArgs(), LocaleContextHolder.getLocale());
        log.warn("API request exception [{}]: {}", status, message);
        ApiException apiException = new ApiException(message, status, ZonedDateTime.now());
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiException> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        HttpStatus status = HttpStatus.CONFLICT;
        String message = messageSource.getMessage("error.user.duplicate", null, LocaleContextHolder.getLocale());
        log.warn("Data integrity violation: {}", e.getMostSpecificCause().getMessage());
        ApiException apiException = new ApiException(message, status, ZonedDateTime.now());
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiException> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("Validation failed: {}", message);
        ApiException apiException = new ApiException(message, HttpStatus.BAD_REQUEST, ZonedDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiException> handleNoResourceFound(NoResourceFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        log.debug("No static resource found: {}", e.getResourcePath());
        ApiException apiException = new ApiException(e.getMessage(), status, ZonedDateTime.now());
        return new ResponseEntity<>(apiException, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiException> handleGeneral(Exception e) {
        String message = messageSource.getMessage("error.general", null, LocaleContextHolder.getLocale());
        log.error("Unhandled exception", e);
        ApiException apiException = new ApiException(message, HttpStatus.INTERNAL_SERVER_ERROR, ZonedDateTime.now());
        return new ResponseEntity<>(apiException, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
