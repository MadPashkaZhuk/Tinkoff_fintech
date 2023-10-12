package org.weather.exception.city;

import org.springframework.http.HttpStatus;

public class CityAlreadyExistsException extends BaseCityException {
    public CityAlreadyExistsException(HttpStatus status, String exceptionMessage) {
        super(status, exceptionMessage);
    }
}
