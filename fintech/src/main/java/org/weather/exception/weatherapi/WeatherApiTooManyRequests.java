package org.weather.exception.weatherapi;

import org.springframework.http.HttpStatus;

public class WeatherApiTooManyRequests extends BaseWeatherApiException {
    public WeatherApiTooManyRequests(HttpStatus status, String exceptionMessage, int errorCode) {
        super(status, exceptionMessage, errorCode);
    }
}
