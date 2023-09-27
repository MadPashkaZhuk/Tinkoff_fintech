package org.weather.advice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.weather.exception.WeatherAlreadyExistsException;
import org.weather.exception.WeatherNotFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(x -> errors.put(x.getField(), x.getDefaultMessage()));
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(WeatherNotFoundException.class)
    public Map<String, String> handleWeatherNotFoundException(WeatherNotFoundException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("errorMessage", exception.getMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(WeatherAlreadyExistsException.class)
    public Map<String, String> handleWeatherAlreadyExistsException(WeatherAlreadyExistsException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("errorMessage", exception.getMessage());
        return errors;
    }
}
