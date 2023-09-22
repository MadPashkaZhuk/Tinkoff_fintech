package org.weather.entity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private long regionId;
    private String regionName;
    private int temperatureValue;
    private LocalDateTime dateTime;
}

