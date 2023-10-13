package org.weather.service;

import org.weather.dto.WeatherApiDTO;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface WeatherControllerService {
    Map<String, List<Weather>> findAll();
    List<Weather> findById(UUID id);
    UUID getIdByRegionName(String regionName);
    void deleteRegion(String regionName);
    List<Weather> createNewWeather(String regionName, WeatherDTO newWeatherDTO);
    List<Weather> updateWeatherTemperature(String regionName, WeatherDTO newWeatherDTO);
    List<Weather> findWeatherListByRegionAndCurrentDay(String regionName);
    double getTemperatureFromWeatherApi(WeatherApiDTO weatherApiDTO);
}
