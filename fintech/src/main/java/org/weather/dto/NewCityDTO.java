package org.weather.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class NewCityDTO {
    @Schema(name = "City name", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "name of the city", example = "Minsk")
    @NotNull(message = "Name of the city can't be null")
    String newName;

    @JsonCreator
    public NewCityDTO(String newName) {
        this.newName = newName;
    }
}
