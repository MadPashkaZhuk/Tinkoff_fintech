package org.weather.service;

import org.springframework.stereotype.Service;
import org.weather.dto.weatherapi.WeatherApiDTO;

@Service
public class WeatherApiService {
    public double getTemperatureFromWeatherApi(WeatherApiDTO weatherApiDTO) {
        return weatherApiDTO.getCurrent().getTemperatureInCelsius();
    }
}
