package org.weather.service.impl;

import org.weather.entity.Weather;
import org.weather.service.WeatherUtilityService;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultWeatherUtilityService implements WeatherUtilityService {

    private static class SingletonHelper {
        private static final DefaultWeatherUtilityService INSTANCE = new DefaultWeatherUtilityService();
    }

    public static DefaultWeatherUtilityService getInstance() {
        return DefaultWeatherUtilityService.SingletonHelper.INSTANCE;
    }

    private DefaultWeatherUtilityService() {}

    @Override
    public Map<String, Double> getAverageTemperatureInRegion(Collection<Weather> weathers) {
        return weathers.stream()
        .collect(Collectors.groupingBy(Weather::getRegionName,
                Collectors.averagingDouble(Weather::getTemperatureValue)));
    }

    @Override
    public Set<String> regionFilteredByTemperature(Collection<Weather> weathers, double temperature) {
        return weathers.stream()
                .filter(x -> Double.compare(x.getTemperatureValue(), temperature) > 0)
                .map(Weather::getRegionName)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Double, List<Weather>> weatherGroupingByTemperature(Collection<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getTemperatureValue));
    }

    @Override
    public Map<UUID, List<Double>> temperatureGroupingById(Collection<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getRegionId,
                        Collectors.mapping(Weather::getTemperatureValue, Collectors.toList())));
    }
}
