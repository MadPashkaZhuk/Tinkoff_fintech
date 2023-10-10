package org.weather.exception.weatherapi;

import org.springframework.http.HttpStatus;

public class WeatherApiDisabledKeyException extends BaseWeatherApiException {
    public WeatherApiDisabledKeyException(HttpStatus status, String exceptionMessage, int errorCode) {
        super(status, exceptionMessage, errorCode);
    }
}
