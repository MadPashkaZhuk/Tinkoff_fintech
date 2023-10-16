package org.weather.exception.weatherapi;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseWeatherApiException extends RuntimeException {
    private final int errorCode;
    private final HttpStatus status;
    private final String exceptionMessage;
    public BaseWeatherApiException(HttpStatus status, String exceptionMessage, int errorCode) {
        super(exceptionMessage);
        this.exceptionMessage = exceptionMessage;
        this.status = status;
        this.errorCode = errorCode;
    }
}
