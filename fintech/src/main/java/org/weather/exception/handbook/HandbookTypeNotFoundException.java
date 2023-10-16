package org.weather.exception.handbook;

import org.springframework.http.HttpStatus;

public class HandbookTypeNotFoundException extends BaseHandbookException {
    public HandbookTypeNotFoundException(HttpStatus status, String message) {
        super(status, message);
    }
}
