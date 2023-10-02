package org.weather.repository;

import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WeatherRepository {
    Map<String, List<Weather>> findAll();
    List<Weather> findById(UUID id);
    void saveRegion(String regionName);
    void saveWeather(String regionName, WeatherDTO newWeatherDTO);
    UUID getIdByRegionName(String regionName);
    void updateWeatherWithSameRegionAndDate(UUID id, String regionName, WeatherDTO newWeatherDTO);
    void deleteRegion(UUID currentId, String regionName);
    boolean hasWeatherWithSameIdAndDate(UUID id, LocalDateTime dateTime);
}
