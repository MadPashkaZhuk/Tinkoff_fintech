package org.weather.service;

import org.weather.dto.WeatherDTO;
import org.weather.entity.WeatherEntity;

import java.util.List;

public interface WeatherService {
    List<WeatherEntity> findAll();
    List<WeatherEntity> getWeatherForCity(String cityName);
    WeatherEntity saveWeatherForCity(String cityName, WeatherDTO weatherDTO);
    void deleteWeatherByDateTime(String cityName, WeatherDTO weatherDTO);
    WeatherEntity updateWeatherForCity(String cityName, WeatherDTO weatherDTO);
}
