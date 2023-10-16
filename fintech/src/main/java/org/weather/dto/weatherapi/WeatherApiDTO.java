package org.weather.dto.weatherapi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
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

    @Value
    public static class Location {
        String name;
        String localtime;
        @JsonCreator
        public Location(@JsonProperty("name") String name, @JsonProperty("localtime") String localtime) {
            this.name = name;
            this.localtime = localtime;
        }
    }
    @Value
    public static class Current {
        double temperatureInCelsius;
        @JsonCreator
        public Current(@JsonProperty("temp_c") double temperatureInCelsius) {
            this.temperatureInCelsius = temperatureInCelsius;
        }
    }

    @Value
    public static class Error {
        int code;
        String message;
    }
}
