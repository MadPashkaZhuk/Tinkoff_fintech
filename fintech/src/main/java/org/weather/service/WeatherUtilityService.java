package org.weather.service;

import org.weather.entity.Weather;

import java.util.*;

public interface WeatherUtilityService {
    Map<String, Double> getAverageTemperatureInRegion(Collection<Weather> weathers);

    Set<String> regionFilteredByTemperature(Collection<Weather> weathers, double temperature);

    Map<Double, List<Weather>> weatherGroupingByTemperature(Collection<Weather> weathers);

    Map<UUID, List<Double>> temperatureGroupingById(Collection<Weather> weathers);
}
