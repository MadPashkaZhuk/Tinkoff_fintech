package org.weather;

import org.weather.entity.Weather;
import org.weather.service.DefaultWeatherService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Main {
    public static List<Weather> generateWeatherList() {
        Map<Long, String> regions = new HashMap<>(Map.of(1L, "Minsk", 2L, "Gomel", 3L,"Brest",
                4L, "Vitebsk", 5L, "Grodno", 6L, "Mogilev"));
        List<Weather> weathers = new ArrayList<>();

        Random random = new Random();
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();

        int minTemp = -20;
        int maxTemp = 30;

        for(int i = 0; i < 10; i++) {
            LocalDate randomDay = LocalDate.ofEpochDay(random.nextLong(minDay, maxDay));
            LocalTime randomTime = LocalTime.of(random.nextInt(24), random.nextInt(60));

            // id in bounds: [1,6], temperature in bounds: [20, 30)
            Weather curr = new Weather(i % 6 + 1, regions.get((long)(i % 6 + 1)),
                    random.nextInt(minTemp, maxTemp), LocalDateTime.of(randomDay, randomTime));
            weathers.add(curr);
        }
        return weathers;
    }

    public static void main(String[] args) {
        List<Weather> weathers = generateWeatherList();
        for(Weather w : weathers) {
            System.out.println(w);
        }

        System.out.println("\nAverage temperature: " + DefaultWeatherService.getAverageTemperature(weathers) + '\n');

        int lowerTemperature = 15;
        System.out.println("Regions where temperature is more than " + lowerTemperature + ": " +
                DefaultWeatherService.regionFilterByTemperature(weathers, lowerTemperature) + '\n');

        System.out.println("Weather grouped by temperature: ");
        for(Map.Entry<Integer, List<Weather>> w :
                DefaultWeatherService.weatherGroupingByTemperature(weathers).entrySet()) {
            System.out.println(w.getKey() + " - " + w.getValue());
        }

        System.out.println("\nTemperature grouped by id: ");
        for(Map.Entry<Long, List<Integer>> w :
                DefaultWeatherService.temperatureGroupingById(weathers).entrySet()) {
            System.out.println(w.getKey() + " - " + w.getValue());
        }
    }
}
