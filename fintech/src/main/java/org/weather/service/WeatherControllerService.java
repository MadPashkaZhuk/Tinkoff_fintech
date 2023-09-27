package org.weather.service;

import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WeatherControllerService {
    Map<UUID, List<Weather>> findAll();
    Optional<List<Weather>> findById(UUID id);
    void saveRegion(String regionName);
    void saveWeather(String regionName, WeatherDTO newWeatherDTO);
    UUID getIdByRegionName(String regionName);
    void updateWeatherWithSameRegionAndDate(UUID id, String regionName, WeatherDTO newWeatherDTO);
    void deleteRegion(UUID currentId, String regionName);
    boolean hasWeatherWithSameIdAndDate(UUID id, LocalDateTime dateTime);

    void createNewWeather(String regionName, WeatherDTO newWeatherDTO);
    void updateWeatherTemperature(UUID id, String regionName, WeatherDTO newWeatherDTO);
}