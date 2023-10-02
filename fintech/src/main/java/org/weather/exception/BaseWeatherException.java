package org.weather.exception;

import lombok.Value;

@Value
public class BaseWeatherException extends RuntimeException{
    int status;
    String exceptionMessage;
}
