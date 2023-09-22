package org.weather.service.impl;

import org.weather.entity.Weather;
import org.weather.service.WeatherService;

import java.util.List;
import java.util.Map;
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
    public double getAverageTemperature(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.averagingDouble(Weather::getTemperatureValue));
    }

    @Override
    public List<String> regionFilterByTemperature(List<Weather> weathers, int temperature) {
        return weathers.stream()
                .filter(x -> x.getTemperatureValue() > temperature)
                .map(Weather::getRegionName)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Map<Integer, List<Weather>> weatherGroupingByTemperature(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getTemperatureValue));
    }

    @Override
    public Map<Long, List<Integer>> temperatureGroupingById(List<Weather> weathers) {
        return weathers.stream()
                .collect(Collectors.groupingBy(Weather::getRegionId,
                        Collectors.mapping(Weather::getTemperatureValue, Collectors.toList())));
    }
}
