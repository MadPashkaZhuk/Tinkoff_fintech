package org.weather.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.weather.dto.BaseExceptionDTO;
import org.weather.dto.BaseWeatherApiExceptionDTO;
import org.weather.exception.BaseWeatherException;
import org.weather.exception.weatherapi.BaseWeatherApiException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    @ExceptionHandler(BaseWeatherApiException.class)
    public ResponseEntity<BaseWeatherApiExceptionDTO> handleBaseWeatherApiException(BaseWeatherApiException ex) {
        BaseWeatherApiExceptionDTO baseWeatherApiExceptionDTO =
                new BaseWeatherApiExceptionDTO(ex.getStatus(), ex.getExceptionMessage(), ex.getErrorCode());
        return new ResponseEntity<>(baseWeatherApiExceptionDTO, ex.getStatus());
    }

    @ExceptionHandler(BaseWeatherException.class)
    public ResponseEntity<BaseExceptionDTO> handleBaseException(BaseWeatherException ex) {
        BaseExceptionDTO baseExceptionDTO = new BaseExceptionDTO(ex.getStatus(), ex.getExceptionMessage());
        return new ResponseEntity<>(baseExceptionDTO, ex.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(x -> errors.put(x.getField(), x.getDefaultMessage()));
        return errors;
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<BaseExceptionDTO> handleUnexpectedExceptions(Throwable exception) {
        BaseExceptionDTO baseExceptionDTO =
                new BaseExceptionDTO(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
        return new ResponseEntity<>(baseExceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
