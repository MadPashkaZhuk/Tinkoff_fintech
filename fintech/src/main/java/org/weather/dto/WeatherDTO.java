package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class WeatherDTO {
    @Schema(name = "temperature", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Current temperature", example = "18.3")
    double temp_val;
    @Schema(name = "dateTime", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Info about date and time", example = "2023-10-28T14:30:00")
    @NotNull(message = "Date and time can't be null")
    LocalDateTime dateTime;
    @Schema(name = "dateTime", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Id of handbook type", example = "3")
    @NotNull(message = "handbook_id can't be null")
    Integer handbook_id;
}
