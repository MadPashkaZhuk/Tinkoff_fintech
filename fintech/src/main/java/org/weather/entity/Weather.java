package org.weather.entity;

import lombok.*;

import java.time.LocalDateTime;

@Value
public class Weather {
    long regionId;
    String regionName;
    int temperatureValue;
    LocalDateTime dateTime;
}

