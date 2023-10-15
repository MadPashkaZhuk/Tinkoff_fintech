package org.weather.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "handbook")
@Getter
@Setter
public class HandbookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(name = "Handbook ID", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Unique identifier in bounds: [1,9]", example = "3")
    private int id;

    @Column(name = "type")
    @Schema(name = "Specific type for weather", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Specific type for weather in list: [sunshine, cloudy, partly cloudy, raining, snowing, " +
                    "overcast, foggy, thunder&lightning, windy]", example = "raining")
    private String weatherType;
}
