package org.weather.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.weather.dto.ExternalWeatherApiResponseDTO;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;
import org.weather.exception.WeatherAlreadyExistsException;
import org.weather.exception.WeatherNotFoundException;
import org.weather.repository.WeatherRepository;
import org.weather.service.WeatherControllerService;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultWeatherControllerService implements WeatherControllerService {
    private final WeatherRepository weatherRepository;
    private final MessageSource messageSource;
    private final RestTemplate restTemplate;
    private final String weatherAlreadyExistsMessageName = "weather.already.exists.message";
    private final String weatherNotFoundMessageName = "weather.not.found.message";
    private final String weatherApiKey = "57ca5d93a8584509b18115355230310";
    private final String weatherApiUrlPath = "https://api.weatherapi.com/v1/current.json";
    @Override
    public Map<String, List<Weather>> findAll() {
        return weatherRepository.findAll();
    }

    @Override
    public List<Weather> findById(UUID id) {
        return weatherRepository.findById(id);
    }

    @Override
    public UUID getIdByRegionName(String regionName) {
        return weatherRepository.getIdByRegionName(regionName);
    }

    @Override
    public void deleteRegion(String regionName) {
        UUID id = this.getIdByRegionName(regionName);
        if(id != null) {
            weatherRepository.deleteRegion(id, regionName);
        }
    }

    @Override
    public List<Weather> createNewWeather(String regionName, WeatherDTO newWeatherDTO) {
        UUID id = this.getIdByRegionName(regionName);
        if(id != null && this.weatherRepository
                .hasWeatherWithSameIdAndDate(id, newWeatherDTO.getDateTime())) {
            throw new WeatherAlreadyExistsException(HttpStatus.BAD_REQUEST, messageSource
                    .getMessage(weatherAlreadyExistsMessageName,null, Locale.getDefault()));
        }
        this.weatherRepository.saveWeather(regionName, newWeatherDTO);
        return this.findById(this.getIdByRegionName(regionName));
    }

    @Override
    public List<Weather> updateWeatherTemperature(String regionName, WeatherDTO newWeatherDTO) {
        UUID id = getValidatedIdByRegionName(regionName);
        if(this.weatherRepository.hasWeatherWithSameIdAndDate(id, newWeatherDTO.getDateTime())) {
            this.weatherRepository.updateWeatherWithSameRegionAndDate(id, regionName, newWeatherDTO);
        }
        else {
            this.weatherRepository.saveWeather(regionName, newWeatherDTO);
        }
        return this.findWeatherListByRegionAndCurrentDay(regionName);
    }

    @Override
    public List<Weather> findWeatherListByRegionAndCurrentDay(String regionName) {
        UUID id = getValidatedIdByRegionName(regionName);
        List<Weather> allWeatherForRegion = this.findById(id);
        return allWeatherForRegion.stream()
                .filter(x -> (x.getDateTime().toLocalDate().equals(LocalDate.now())))
                .toList();
    }

    public UUID getValidatedIdByRegionName(String regionName) {
        UUID id = this.getIdByRegionName(regionName);
        if(id == null) {
            throw new WeatherNotFoundException(HttpStatus.NOT_FOUND, messageSource
                    .getMessage(weatherNotFoundMessageName,null,Locale.getDefault()));
        }
        return id;
    }

    @Override
    public double getTemperatureFromExternalApi(String regionName) {
        String finalPath = weatherApiUrlPath + "?key=" + weatherApiKey + "&q=" + regionName + "&aqi=no";
        ExternalWeatherApiResponseDTO weatherApiResponseDTO = this.restTemplate.getForObject(finalPath, ExternalWeatherApiResponseDTO.class);
        return weatherApiResponseDTO.getCurrent().getTemperatureInCelsius();
    }
}
