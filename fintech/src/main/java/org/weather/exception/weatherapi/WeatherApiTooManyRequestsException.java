package org.weather.exception.weatherapi;

import org.springframework.http.HttpStatus;

public class WeatherApiTooManyRequestsException extends BaseWeatherApiException {
    public WeatherApiTooManyRequestsException(HttpStatus status, String exceptionMessage, int errorCode) {
        super(status, exceptionMessage, errorCode);
    }
}
