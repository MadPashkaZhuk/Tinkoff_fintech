package org.weather.exception.weather;

import org.springframework.http.HttpStatus;

public class WeatherAlreadyExistsException extends BaseWeatherException {
    public WeatherAlreadyExistsException(HttpStatus status, String exceptionMessage) {
        super(status, exceptionMessage);
    }
}
