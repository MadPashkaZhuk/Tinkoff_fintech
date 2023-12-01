package org.weather.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.weather.dto.NewWeatherDTO;
import org.weather.service.WeatherService;

@RequiredArgsConstructor
@Component
@Slf4j
public class WeatherConsumer {
    private final WeatherService weatherService;

    @KafkaListener(topics = "weather-topic", groupId = "weather-group")
    public void consumeWeatherDto(String cityName, NewWeatherDTO dto) {
        weatherService.updateWeatherForCity(cityName, dto);
        Double averageTempInLast30Records = weatherService.getAverageForCity(cityName);
        log.info("Moving avg for {}: {}", cityName, averageTempInLast30Records);
    }
}
