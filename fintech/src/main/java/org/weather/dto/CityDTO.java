package org.weather.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class CityDTO {
    @Schema(name = "CityEntity name", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "name of the city", example = "Minsk")
    @NotNull(message = "Name of the city can't be null")
    String newName;

    @JsonCreator
    public CityDTO(String newName) {
        this.newName = newName;
    }
}
