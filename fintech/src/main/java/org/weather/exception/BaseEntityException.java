package org.weather.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseEntityException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    public BaseEntityException (HttpStatus status, String message) {
        super(message);
        this.message = message;
        this.status = status;
    }
}