package org.weather.dto.weatherapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WeatherApiDTO {
    @Schema(name = "Location", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Contains info about Location name and local time")
    Location location;
    @Schema(name = "Temperature info", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Contains info temperature and other forecast's data")
    Current current;
    @Schema(name = "Info about error thrown", requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            description = "Contains info about error thrown: code and message")
    Error error;

    @Data
    public static class Location {
        String name;
        String localtime;
    }
    @Data
    public static class Current {
        @JsonProperty("temp_c")
        double temperatureInCelsius;
    }

    @Data
    public static class Error {
        private int code;
        private String message;
    }
}
