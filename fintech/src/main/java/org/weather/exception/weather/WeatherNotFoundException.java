package org.weather.exception.weather;

import org.springframework.http.HttpStatus;

public class WeatherNotFoundException extends BaseWeatherException {
    public WeatherNotFoundException(HttpStatus status, String exceptionMessage) {
        super(status, exceptionMessage);
    }
}
