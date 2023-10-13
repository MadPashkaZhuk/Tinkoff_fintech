package org.weather.exception.weatherapi;

import org.springframework.http.HttpStatus;

public class WeatherApiUnknownException extends BaseWeatherApiException {
    public WeatherApiUnknownException(HttpStatus status, String exceptionMessage, int errorCode) {
        super(status, exceptionMessage, errorCode);
    }
}
