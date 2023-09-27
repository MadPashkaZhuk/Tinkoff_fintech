package org.weather.service.impl;

import org.springframework.stereotype.Service;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;
import org.weather.exception.WeatherAlreadyExistsException;
import org.weather.repository.WeatherRepository;
import org.weather.service.WeatherControllerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultWeatherControllerService implements WeatherControllerService {
    private final WeatherRepository weatherRepository;
    private final String WEATHER_ALREADY_EXISTS_MESSAGE = "Weather with same region and date already exists. " +
            "If you need to update it, use Put method";

    public DefaultWeatherControllerService(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @Override
    public Map<UUID, List<Weather>> findAll() {
        return weatherRepository.findAll();
    }

    @Override
    public Optional<List<Weather>> findById(UUID id) {
        return weatherRepository.findById(id);
    }

    @Override
    public void saveRegion(String regionName) {
        regionName = regionName.toLowerCase();
        weatherRepository.saveRegion(regionName);
    }

    @Override
    public void saveWeather(String regionName, WeatherDTO newWeatherDTO) {
        regionName = regionName.toLowerCase();
        weatherRepository.saveWeather(regionName, newWeatherDTO);
    }

    @Override
    public UUID getIdByRegionName(String regionName) {
        regionName = regionName.toLowerCase();
        return weatherRepository.getIdByRegionName(regionName);
    }

    @Override
    public void updateWeatherWithSameRegionAndDate(UUID id, String regionName, WeatherDTO newWeatherDTO) {
        regionName = regionName.toLowerCase();
        this.weatherRepository.updateWeatherWithSameRegionAndDate(id, regionName, newWeatherDTO);
    }

    @Override
    public void deleteRegion(UUID currentId, String regionName) {
        regionName = regionName.toLowerCase();
        weatherRepository.deleteRegion(currentId, regionName);
    }

    @Override
    public boolean hasWeatherWithSameIdAndDate(UUID id, LocalDateTime dateTime) {
        return weatherRepository.hasWeatherWithSameIdAndDate(id, dateTime);
    }

    @Override
    public void createNewWeather(String regionName, WeatherDTO newWeatherDTO) {
        regionName = regionName.toLowerCase();
        UUID currentId = this.weatherRepository.getIdByRegionName(regionName);
        if(currentId != null && this.weatherRepository
                .hasWeatherWithSameIdAndDate(currentId, newWeatherDTO.getDateTime())) {
            throw new WeatherAlreadyExistsException(WEATHER_ALREADY_EXISTS_MESSAGE);
        }
        this.weatherRepository.saveWeather(regionName, newWeatherDTO);
    }

    @Override
    public void updateWeatherTemperature(UUID id, String regionName, WeatherDTO newWeatherDTO) {
        regionName = regionName.toLowerCase();
        if(this.weatherRepository.hasWeatherWithSameIdAndDate(id, newWeatherDTO.getDateTime())) {
            this.weatherRepository.updateWeatherWithSameRegionAndDate(id, regionName, newWeatherDTO);
        }
        else {
            this.weatherRepository.saveWeather(regionName, newWeatherDTO);
        }
    }
}
