package org.weather.exception.sql;

import org.springframework.http.HttpStatus;
import org.weather.exception.BaseEntityException;

public class BaseSqlException extends BaseEntityException {
    public BaseSqlException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
}
