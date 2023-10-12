package org.weather.exception.city;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseCityException extends RuntimeException{
    private final HttpStatus status;
    private final String exceptionMessage;
    public BaseCityException(HttpStatus status, String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
        this.status = status;
    }
}
