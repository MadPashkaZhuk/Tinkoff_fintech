package org.weather.service;

import org.weather.entity.Weather;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WeatherService {
    Map<String, Double> getAverageTemperatureInRegion(Collection<Weather> weathers);

    Set<String> regionFilteredByTemperature(Collection<Weather> weathers, int temperature);

    Map<Integer, List<Weather>> weatherGroupingByTemperature(Collection<Weather> weathers);

    Map<Long, List<Integer>> temperatureGroupingById(Collection<Weather> weathers);
}
