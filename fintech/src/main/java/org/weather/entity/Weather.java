package org.weather.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Schema
public class Weather {
    @Schema(name = "Weather ID", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Unique identifier", example = "7f1e514f-8ccc-4817-b6ee-c362250cffda")
    UUID regionId;
    @Schema(name = "Region", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Region name stored in lowercase", example = "Minsk")
    String regionName;
    @Schema(name = "Temperature", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Double temperature value, no restrictions for range", example = "100")
    double temperatureValue;
    @Schema(type = "string", name = "Weather Date and Time", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Weather date info", example = "2023-09-28T14:30:00")
    LocalDateTime dateTime;
}
