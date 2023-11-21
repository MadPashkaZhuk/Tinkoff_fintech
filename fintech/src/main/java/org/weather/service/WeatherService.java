package org.weather.service;

import org.weather.dto.NewWeatherDTO;
import org.weather.dto.WeatherDTO;

import java.util.List;

public interface WeatherService {
    List<WeatherDTO> findAll();
    List<WeatherDTO> getWeatherHistoryForCity(String cityName);
    WeatherDTO getWeatherForCity(String cityName);
    WeatherDTO saveWeatherForCity(String cityName, NewWeatherDTO newWeatherDTO);
    void deleteWeatherByDateTime(String cityName, NewWeatherDTO newWeatherDTO);
    WeatherDTO updateWeatherForCity(String cityName, NewWeatherDTO newWeatherDTO);
}
