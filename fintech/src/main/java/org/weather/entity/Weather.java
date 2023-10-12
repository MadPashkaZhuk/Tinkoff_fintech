package org.weather.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "weather")
@Getter
@NoArgsConstructor
public class Weather {
    @Id
    @UuidGenerator
    @Schema(name = "Weather ID", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Unique identifier", example = "7f1e514f-8ccc-4817-b6ee-c362250cffda")
    private UUID id;

    @Column(name = "temperature")
    @Schema(name = "Temperature", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Double temperature value, no restrictions for range", example = "100")
    private double temp_c;

    @ManyToOne(cascade = CascadeType.REMOVE, optional = false)
    @Schema(name = "City", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "City name", example = "Minsk")
    private City city;

    @Column(name = "date_time")
    @Schema(type = "string", name = "Weather Date and Time", requiredMode = Schema.RequiredMode.REQUIRED,
            description = "Weather date info", example = "2023-09-28T14:30:00")
    private LocalDateTime datetime;

    public Weather(double temp_c, City city, LocalDateTime datetime) {
        this.temp_c = temp_c;
        this.city = city;
        this.datetime = datetime;
    }
}
