package org.weather.exception.city;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.weather.exception.BaseEntityException;

@Getter
public class BaseCityException extends BaseEntityException {
    public BaseCityException(HttpStatus status, String message) {
        super(status, message);
    }
}
