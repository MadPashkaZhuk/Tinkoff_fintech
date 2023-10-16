package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class WeatherDTO {
    @Schema(name = "id", description = "Weather id.", example = "e3d8b008-90c3-466e-918a-c9e7e4656996")
    UUID id;
    @Schema(name = "temperature", description = "Current temperature", example = "18.3")
    double temperature;
    @Schema(name = "dateTime", description = "Info about date and time", example = "2023-10-28T14:30:00")
    LocalDateTime dateTime;
    @Schema(name = "city", description = "CityDTO object",
            example = "name = Minsk, id = e3d8b008-90c3-466e-918a-c9e7e4656996")
    CityDTO city;
    @Schema(name = "dateTime", description = "HandbookDTO object", example = "type = sunshine, id = 3")
    HandbookDTO handbook;
}
