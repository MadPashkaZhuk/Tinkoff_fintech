package org.weather.exception.handbook;

import org.springframework.http.HttpStatus;

public class HandbookAlreadyExistsException extends BaseHandbookException {
    public HandbookAlreadyExistsException(HttpStatus status, String message) {
        super(status, message);
    }
}
