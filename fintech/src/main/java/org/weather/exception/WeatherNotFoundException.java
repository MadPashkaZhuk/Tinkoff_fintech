package org.weather.exception;

public class WeatherNotFoundException extends RuntimeException{
    public WeatherNotFoundException(String message) {
        super(message);
    }
}
