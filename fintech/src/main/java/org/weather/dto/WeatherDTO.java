package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class WeatherDTO {
    @Schema(name = "Temperature", requiredMode = Schema.RequiredMode.REQUIRED, description = "Double temperature value, no restrictions for range", example = "100")
    double temperatureValue;
    @NotNull(message = "Date and time can't be null")
    @Schema(type = "string", name = "Weather Date and Time", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Weather date info", example = "2023-09-28T14:30:00")
    LocalDateTime dateTime;
}
