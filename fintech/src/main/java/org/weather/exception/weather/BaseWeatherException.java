package org.weather.exception.weather;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.weather.exception.BaseEntityException;

@Getter
public class BaseWeatherException extends BaseEntityException {
    public BaseWeatherException(HttpStatus status, String message) {
        super(status, message);
    }
}
