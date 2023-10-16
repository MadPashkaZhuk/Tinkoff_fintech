package org.weather.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;

@Value
public class HandbookDTO {
    @Schema(name = "Id", description = "Unique id in bounds: [1,9]", example = "4")
    Integer id;
    @Schema(name = "handbook type", description = "handbook type of current weather", example = "sunshine")
    String type;
}
