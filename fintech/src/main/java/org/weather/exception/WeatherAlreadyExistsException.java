package org.weather.exception;

public class WeatherAlreadyExistsException extends RuntimeException{
    public WeatherAlreadyExistsException(String message) {
        super(message);
    }
}
