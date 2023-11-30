package org.weather.kafka;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.weather.client.WeatherApiRestClient;
import org.weather.dto.HandbookDTO;
import org.weather.dto.NewWeatherDTO;
import org.weather.dto.weatherapi.WeatherApiDTO;
import org.weather.service.HandbookService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@EnableScheduling
@Component
public class WeatherApiScheduler {
    private final WeatherApiRestClient weatherApiRestClient;
    private final WeatherProducer weatherProducer;
    private final AverageTopicProperties averageTopicProperties;
    private final HandbookService handbookService;
    private final AtomicInteger currentCityIndex = new AtomicInteger(0);

    public WeatherApiScheduler(WeatherApiRestClient weatherApiRestClient, WeatherProducer weatherProducer,
                               AverageTopicProperties averageTopicProperties, HandbookService handbookService) {
        this.weatherApiRestClient = weatherApiRestClient;
        this.weatherProducer = weatherProducer;
        this.averageTopicProperties = averageTopicProperties;
        this.handbookService = handbookService;
    }

    @Scheduled(cron = "${topic.average.cron}")
    public void scheduledAvgCalculation() {
        currentCityIndex.set(currentCityIndex.get() % averageTopicProperties.getCities().size());
        String currentCity = averageTopicProperties.getCities().get(currentCityIndex.getAndIncrement());
        WeatherApiDTO weatherApiDTO = weatherApiRestClient.getDTOFromWeatherApi(currentCity);
        weatherProducer.sendWeatherDto(currentCity, convertFromApiResponse(weatherApiDTO));
    }

    private NewWeatherDTO convertFromApiResponse(WeatherApiDTO dto) {
        double temperature = dto.getCurrent().getTemperatureInCelsius();
        String handbookType = dto.getCurrent().getCondition().getText();
        HandbookDTO handbookDTO = handbookService.getOrCreateByTypeName(handbookType);
        LocalDateTime dateTime = LocalDateTime.parse(dto.getLocation().getLocaltime(), getDateTimeFormatter());
        return new NewWeatherDTO(temperature, dateTime, handbookDTO.getId());
    }

    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-d H:m");
    }
}
