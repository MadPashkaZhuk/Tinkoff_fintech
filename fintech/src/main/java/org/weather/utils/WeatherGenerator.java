package org.weather.utils;

import org.weather.entity.Weather;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class WeatherGenerator {

    //generates list with Belarus' regions and random LocalDateTime and temperature in specific bounds
    public static List<Weather> generateWeatherList() {
        List<Weather> weathers = new ArrayList<>();

        Map<Long, String> regions = new HashMap<>(Map.of(1L, "Minsk", 2L, "Gomel", 3L,"Brest",
                4L, "Vitebsk", 5L, "Grodno", 6L, "Mogilev"));

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
}
