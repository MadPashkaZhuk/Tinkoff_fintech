package org.weather.service;

import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;

import java.util.List;

public interface WeatherService {
    List<Weather> findAll();
    List<Weather> getWeatherForCity(String cityName);
    Weather saveWeatherForCity(String cityName, WeatherDTO weatherDTO);
    void deleteWeatherByDateTime(String cityName, WeatherDTO weatherDTO);
    Weather updateWeatherForCity(String cityName, WeatherDTO weatherDTO);
}
