package org.weather.service.impl;

import org.weather.entity.Weather;
import org.weather.service.WeatherService;

import java.util.*;
import java.util.stream.Collectors;

public class DefaultWeatherService implements WeatherService {

    private static class SingletonHelper {
        private static final DefaultWeatherService INSTANCE = new DefaultWeatherService();
    }

    public static DefaultWeatherService getInstance() {
        return DefaultWeatherService.SingletonHelper.INSTANCE;
    }

    private DefaultWeatherService() {}

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
