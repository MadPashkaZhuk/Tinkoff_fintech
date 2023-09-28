package org.weather.service;

import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface WeatherControllerService {
    Map<UUID, List<Weather>> findAll();
    Optional<List<Weather>> findById(UUID id);
    UUID getIdByRegionName(String regionName);
    void deleteRegion(UUID id, String regionName);
    void createNewWeather(String regionName, WeatherDTO newWeatherDTO);
    void updateWeatherTemperature(UUID id, String regionName, WeatherDTO newWeatherDTO);
    List<Weather> findWeatherListByIdAndCurrentDay(UUID id);
}
