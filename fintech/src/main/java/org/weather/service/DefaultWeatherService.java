package org.weather.service;

import org.weather.entity.Weather;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultWeatherService {
    public static double getAverageTemperature(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.averagingDouble(Weather::getTemperatureValue));
    }

    public static List<String> regionFilterByTemperature(List<Weather> weathers, int temperature) {
        return weathers.stream()
                .filter(x -> x.getTemperatureValue() > temperature)
                .map(Weather::getRegionName)
                .distinct()
                .collect(Collectors.toList());
    }

    public static Map<Integer, List<Weather>> weatherGroupingByTemperature(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getTemperatureValue));
    }

    public static Map<Long, List<Integer>> temperatureGroupingById(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getRegionId,
                        Collectors.mapping(Weather::getTemperatureValue, Collectors.toList())));
    }
}
