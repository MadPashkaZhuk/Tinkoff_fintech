package org.weather.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class Weather {
    UUID regionId;
    String regionName;
    double temperatureValue;
    LocalDateTime dateTime;
}
