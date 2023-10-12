package org.weather.exception.weather;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseWeatherException extends RuntimeException{
    private final HttpStatus status;
    private final String exceptionMessage;
    public BaseWeatherException(HttpStatus status, String exceptionMessage) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
        this.status = status;
    }
}
