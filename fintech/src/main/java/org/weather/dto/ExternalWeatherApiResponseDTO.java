package org.weather.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Value;

@Data
public class ExternalWeatherApiResponseDTO {
    @Schema(name = "Location", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Contains info about Location name and local time")
    Location location;
    @Schema(name = "Temperature info", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Contains info temperature and other forecast's data")
    Current current;

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
}
