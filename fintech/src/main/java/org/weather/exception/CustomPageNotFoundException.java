package org.weather.exception;

public class CustomPageNotFoundException extends RuntimeException{
    public CustomPageNotFoundException(String message) {
        super(message);
    }
}
