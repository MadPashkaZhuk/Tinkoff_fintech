package org.weather.utils;

import org.springframework.stereotype.Component;
import org.weather.dto.weatherapi.WeatherApiDTO;

@Component
public class WeatherApiMapper {
    public double getTemperatureFromWeatherApi(WeatherApiDTO weatherApiDTO) {
        return weatherApiDTO.getCurrent().getTemperatureInCelsius();
    }
}
