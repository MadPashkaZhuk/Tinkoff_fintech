package org.weather.exception.handbook;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.weather.exception.BaseEntityException;

@Getter
public class BaseHandbookException extends BaseEntityException {
    public BaseHandbookException(HttpStatus status, String message) {
        super(status, message);
    }
}
