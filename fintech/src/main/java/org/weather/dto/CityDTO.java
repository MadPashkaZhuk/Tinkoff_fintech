package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CityDTO {
    @Schema(name = "City name", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "name of the city", example = "Minsk")
    @NotNull(message = "Name of the city can't be null")
    String newName;
}
