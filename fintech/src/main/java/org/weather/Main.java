package org.weather;

import org.weather.entity.Weather;
import org.weather.service.WeatherService;
import org.weather.service.impl.DefaultWeatherService;
import org.weather.utils.WeatherGenerator;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        WeatherService weatherService = DefaultWeatherService.getInstance();

        List<Weather> weathers = WeatherGenerator.generateWeatherList();
        for(Weather w : weathers) {
            System.out.println(w);
        }

        System.out.println("\nAverage temperature: " + weatherService.getAverageTemperature(weathers) + '\n');

        int lowerTemperature = 15;
        System.out.println("Regions where temperature is more than " + lowerTemperature + ": " +
                weatherService.regionFilterByTemperature(weathers, lowerTemperature) + '\n');

        System.out.println("Weather grouped by temperature: ");
        for(Map.Entry<Integer, List<Weather>> w :
                weatherService.weatherGroupingByTemperature(weathers).entrySet()) {
            System.out.println(w.getKey() + " - " + w.getValue());
        }

        System.out.println("\nTemperature grouped by id: ");
        for(Map.Entry<Long, List<Integer>> w :
                weatherService.temperatureGroupingById(weathers).entrySet()) {
            System.out.println(w.getKey() + " - " + w.getValue());
        }
    }
}
