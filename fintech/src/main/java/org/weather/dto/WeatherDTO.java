package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeatherDTO {
    @Schema(name = "temperature", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Current temperature", example = "18.3")
    double temp_val;
    @Schema(name = "dateTime", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Info about date and time", example = "2023-10-28T14:30:00")
    LocalDateTime dateTime;
}
