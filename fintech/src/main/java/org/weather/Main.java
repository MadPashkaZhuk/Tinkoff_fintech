package org.weather;

import org.weather.entity.Weather;
import org.weather.service.WeatherService;
import org.weather.service.impl.DefaultWeatherService;
import org.weather.utils.WeatherGenerator;

import java.util.*;

public class Main {
    private final static int LOWER_TEMP = 15;

    public static void main(String[] args) {
        WeatherService weatherService = DefaultWeatherService.getInstance();

        List<Weather> weathers = WeatherGenerator.generateWeatherList();
        weathers.forEach(System.out::println);

        Map<String, Double> regionAverageTemperature = weatherService.getAverageTemperatureInRegion(weathers);
        System.out.println("\nAverage temperature in each region: ");
        regionAverageTemperature.forEach((key, val) -> System.out.println(key + " - "  + val));

        Set<String> regionsFilteredByTemperature = weatherService.regionFilteredByTemperature(weathers, LOWER_TEMP);
        System.out.println("\nRegions where temperature is more than " + LOWER_TEMP + ": ");
        regionsFilteredByTemperature.forEach(System.out::println);

        Map<Double, List<Weather>> weatherGroupedByTemp = weatherService.weatherGroupingByTemperature(weathers);
        System.out.println("\nWeather grouped by temperature: ");
        weatherGroupedByTemp.forEach((key, val) -> System.out.println(key + " - " + val));

        Map<Long, List<Double>> tempGroupedById = weatherService.temperatureGroupingById(weathers);
        System.out.println("\nTemperature grouped by id: ");
        tempGroupedById.forEach((key, val) -> System.out.println(key + " - " + val));
    }
}
