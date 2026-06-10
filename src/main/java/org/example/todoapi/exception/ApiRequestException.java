package org.example.todoapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiRequestException extends RuntimeException {

    private final HttpStatus status;
    private final String messageKey;
    private final transient Object[] args;

    public ApiRequestException(String messageKey, HttpStatus status, Object... args) {
        super(messageKey);
        this.status = status;
        this.messageKey = messageKey;
        this.args = args;
    }

}
