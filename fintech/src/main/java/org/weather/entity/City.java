package org.weather.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Table(name = "city")
public class City {
    @Id
    @UuidGenerator
    @Schema(name = "City ID", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Unique identifier", example = "7f1e514f-8ccc-4817-b6ee-c362250cffda")
    private UUID id;

    @Setter
    @Schema(name = "City name", requiredMode = Schema.RequiredMode.REQUIRED, example = "Minsk")
    private String name;
}
