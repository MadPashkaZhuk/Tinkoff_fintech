package org.weather.service;

import org.weather.entity.Weather;

import java.util.List;
import java.util.Map;

public interface WeatherService {
    double getAverageTemperature(List<Weather> weathers);

    List<String> regionFilterByTemperature(List<Weather> weathers, int temperature);

    Map<Integer, List<Weather>> weatherGroupingByTemperature(List<Weather> weathers);

    Map<Long, List<Integer>> temperatureGroupingById(List<Weather> weathers);
}
