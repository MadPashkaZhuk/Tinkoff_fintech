package org.weather.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.time.LocalDateTime;

@Value
public class WeatherDTO {
    double temperatureValue;
    @NotNull(message = "Date and time can't be null")
    LocalDateTime dateTime;
}
