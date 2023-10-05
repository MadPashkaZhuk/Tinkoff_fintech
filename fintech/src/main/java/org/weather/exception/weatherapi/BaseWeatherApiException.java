package org.weather.exception.weatherapi;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.weather.exception.BaseWeatherException;

@Getter
public class BaseWeatherApiException extends BaseWeatherException {
    private final int errorCode;
    public BaseWeatherApiException(HttpStatus status, String exceptionMessage, int errorCode) {
        super(status, exceptionMessage);
        this.errorCode = errorCode;
    }
}
