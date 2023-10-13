package org.weather.exception.weatherapi;

import org.springframework.http.HttpStatus;

public class WeatherApiIncorrectQueryException extends BaseWeatherApiException {
    public WeatherApiIncorrectQueryException(HttpStatus status, String exceptionMessage, int errorCode) {
        super(status, exceptionMessage, errorCode);
    }
}
