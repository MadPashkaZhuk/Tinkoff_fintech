package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WeatherApiErrorDTO {
    @Schema(name = "Error description",
            description = "Error description, that contains error code and message provided by weatherapi.com")
    ErrorDetails error;
    @Data
    public static class ErrorDetails {
        int code;
        String message;
    }
}