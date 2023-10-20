package org.weather;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.weather.client.WeatherApiRestClient;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.NewWeatherDTO;
import org.weather.dto.weatherapi.WeatherApiDTO;
import org.weather.service.CityService;
import org.weather.service.HandbookService;
import org.weather.service.WeatherService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DataInitializationService {
    private final WeatherApiRestClient weatherApiRestClient;
    private final CityService cityService;
    private final WeatherService weatherService;
    private final HandbookService handbookService;

    public DataInitializationService(WeatherApiRestClient weatherApiRestClient,
                                     CityService cityService,
                                     WeatherService weatherService,
                                     HandbookService handbookService) {
        this.weatherApiRestClient = weatherApiRestClient;
        this.cityService = cityService;
        this.weatherService = weatherService;
        this.handbookService = handbookService;
    }

    @PostConstruct
    public void initializeData() {
        List<CityDTO> cities = cityService.findAll();
        cities.forEach(x -> {
            WeatherApiDTO currentResponse = weatherApiRestClient.getDTOFromWeatherApi(x.getName());
            double temperature = currentResponse.getCurrent().getTemperatureInCelsius();
            String handbookType = currentResponse.getCurrent().getCondition().getText();
            HandbookDTO handbookDTO = handbookService.getOrCreateByTypeName(handbookType);
            LocalDateTime dateTime = LocalDateTime.parse(currentResponse.getLocation().getLocaltime(), getDateTimeFormatter());
            NewWeatherDTO weatherDTO = new NewWeatherDTO(temperature, dateTime, handbookDTO.getId());
            weatherService.saveWeatherForCity(x.getName(), weatherDTO);
        });
    }

    private DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-d H:m");
    }
}

