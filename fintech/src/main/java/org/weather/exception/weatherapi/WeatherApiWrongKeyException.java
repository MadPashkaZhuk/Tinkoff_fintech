package org.weather.exception.weatherapi;

import org.springframework.http.HttpStatus;

public class WeatherApiWrongKeyException extends BaseWeatherApiException {
    public WeatherApiWrongKeyException(HttpStatus status, String exceptionMessage, int errorCode) {
        super(status, exceptionMessage, errorCode);
    }
}
