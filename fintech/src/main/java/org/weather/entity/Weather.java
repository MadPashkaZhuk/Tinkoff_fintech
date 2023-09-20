package org.weather.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Weather {
    private long regionId;
    private String regionName;
    private int temperatureValue;
    private LocalDateTime dateTime;

    @Override
    public String toString() {
        return "Weather{" +
                "regionId=" + regionId +
                ", regionName='" + regionName + '\'' +
                ", temperatureValue=" + temperatureValue +
                ", dateTime=" + dateTime +
                '}';
    }
}

