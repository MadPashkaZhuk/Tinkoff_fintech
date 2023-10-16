package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

import java.util.UUID;

@Value
public class CityDTO {
    @Schema(name = "Id", description = "Unique UUID", example = "e3d8b008-90c3-466e-918a-c9e7e4656996")
    UUID id;
    @Schema(name = "City name", description = "name of the city", example = "Minsk")
    String name;
}
