package org.weather.utils;

import org.weather.entity.Weather;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class WeatherGenerator {
    private final static int MIN_TEMP = -20;
    private final static int MAX_TEMP = 30;
    private final static int REGION_SIZE = 6;
    private final static int WEATHER_AMOUNT = 10;
    private final static int HOURS_AMOUNT = 24;
    private final static int MINUTES_AMOUNT = 60;

    //generates list with Belarus' regions and random LocalDateTime and temperature in specific bounds
    public static List<Weather> generateWeatherList() {
        List<String> stringRegions = new ArrayList<>(List.of("Minsk", "Gomel", "Brest", "Vitebsk", "Grodno", "Mogilev"));
        Map<Long, String> regions = LongStream.range(0, stringRegions.size())
                .boxed()
                .collect(Collectors.toMap(i -> i + 1, i -> stringRegions.get(Math.toIntExact(i))));

        Random random = new Random();
        long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
        long maxDay = LocalDate.now().toEpochDay();

        Stream.Builder<Weather> builder = Stream.builder();

        for(int i = 0; i < WEATHER_AMOUNT; i++) {
            LocalDate randomDay = LocalDate.ofEpochDay(random.nextLong(minDay, maxDay));
            LocalTime randomTime = LocalTime.of(random.nextInt(HOURS_AMOUNT), random.nextInt(MINUTES_AMOUNT));

            // id in bounds: [1,REGION_SIZE], temperature in bounds: [MIN_TEMP, MAX_TEMP)
            builder.add(new Weather(i % REGION_SIZE + 1, regions.get((long)(i % REGION_SIZE + 1)),
                    random.nextInt(MIN_TEMP, MAX_TEMP), LocalDateTime.of(randomDay, randomTime)));
        }
        return builder.build().toList();
    }
}
