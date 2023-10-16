package org.weather.exception.city;

import org.springframework.http.HttpStatus;

public class CityNotFoundException extends BaseCityException{
    public CityNotFoundException(HttpStatus status, String exceptionMessage) {
        super(status, exceptionMessage);
    }
}
