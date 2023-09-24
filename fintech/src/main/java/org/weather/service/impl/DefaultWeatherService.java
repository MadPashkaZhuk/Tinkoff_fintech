package org.weather.service.impl;

import org.weather.entity.Weather;
import org.weather.service.WeatherService;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    public Set<String> regionFilteredByTemperature(Collection<Weather> weathers, int temperature) {
        return weathers.stream()
                .filter(x -> x.getTemperatureValue() > temperature)
                .map(Weather::getRegionName)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<Integer, List<Weather>> weatherGroupingByTemperature(Collection<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getTemperatureValue));
    }

    @Override
    public Map<Long, List<Integer>> temperatureGroupingById(Collection<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getRegionId,
                        Collectors.mapping(Weather::getTemperatureValue, Collectors.toList())));
    }
}
