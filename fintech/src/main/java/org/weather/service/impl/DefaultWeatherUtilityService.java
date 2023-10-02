package org.weather.service.impl;

import org.weather.entity.Weather;

import java.util.*;
import java.util.stream.Collectors;

public final class DefaultWeatherUtilityService{

    private DefaultWeatherUtilityService() {}

    public static Map<String, Double> getAverageTemperatureInRegion(Collection<Weather> weathers) {
        return weathers.stream()
        .collect(Collectors.groupingBy(Weather::getRegionName,
                Collectors.averagingDouble(Weather::getTemperatureValue)));
    }

    public static Set<String> regionFilteredByTemperature(Collection<Weather> weathers, double temperature) {
        return weathers.stream()
                .filter(x -> Double.compare(x.getTemperatureValue(), temperature) > 0)
                .map(Weather::getRegionName)
                .collect(Collectors.toSet());
    }

    public static Map<Double, List<Weather>> weatherGroupingByTemperature(Collection<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getTemperatureValue));
    }

    public static Map<UUID, List<Double>> temperatureGroupingById(Collection<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getRegionId,
                        Collectors.mapping(Weather::getTemperatureValue, Collectors.toList())));
    }
}
