package org.weather.entity;

import java.time.LocalDateTime;

public class Weather {
    private long regionId;
    private String regionName;
    private int temperatureValue;
    private LocalDateTime dateTime;

    public Weather() {

    }

    public Weather(long regionId, String regionName, int temperatureValue, LocalDateTime dateTime) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.temperatureValue = temperatureValue;
        this.dateTime = dateTime;
    }
    public long getRegionId() {
        return regionId;
    }

    public void setRegionId(long regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public int getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(int temperatureValue) {
        this.temperatureValue = temperatureValue;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

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

