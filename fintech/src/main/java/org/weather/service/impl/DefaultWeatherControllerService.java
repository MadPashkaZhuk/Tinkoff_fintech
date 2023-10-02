package org.weather.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;
import org.weather.exception.BaseWeatherException;
import org.weather.repository.WeatherRepository;
import org.weather.service.WeatherControllerService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultWeatherControllerService implements WeatherControllerService {
    private final WeatherRepository weatherRepository;
    private final static String WEATHER_ALREADY_EXISTS_MESSAGE = "Weather with same region and date already exists. " +
            "If you need to update it, use Put method";
    private final static String WEATHER_NOT_FOUND_MESSAGE = "Region with this name doesn't exist.";

    @Override
    public Map<UUID, List<Weather>> findAll() {
        return weatherRepository.findAll();
    }

    @Override
    public List<Weather> findById(UUID id) {
        return weatherRepository.findById(id);
    }

    @Override
    public UUID getIdByRegionName(String regionName) {
        regionName = regionName.toLowerCase();
        return weatherRepository.getIdByRegionName(regionName);
    }

    @Override
    public void deleteRegion(String regionName) {
        UUID id = this.getIdByRegionName(regionName);
        if(id != null) {
            regionName = regionName.toLowerCase();
            weatherRepository.deleteRegion(id, regionName);
        }
    }

    @Override
    public List<Weather> createNewWeather(String regionName, WeatherDTO newWeatherDTO) {
        regionName = regionName.toLowerCase();
        UUID id = this.getIdByRegionName(regionName);
        if(id != null && this.weatherRepository
                .hasWeatherWithSameIdAndDate(id, newWeatherDTO.getDateTime())) {
            throw new BaseWeatherException(400, WEATHER_ALREADY_EXISTS_MESSAGE);
        }
        this.weatherRepository.saveWeather(regionName, newWeatherDTO);
        return this.findWeatherListByRegionAndCurrentDay(regionName);
    }

    @Override
    public List<Weather> updateWeatherTemperature(String regionName, WeatherDTO newWeatherDTO) {
        UUID id = this.getIdByRegionName(regionName);
        if(id == null) {
            throw new BaseWeatherException(404, WEATHER_NOT_FOUND_MESSAGE);
        }
        regionName = regionName.toLowerCase();
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
        UUID id = this.getIdByRegionName(regionName);
        if(id == null) {
            throw new BaseWeatherException(404, WEATHER_NOT_FOUND_MESSAGE);
        }
        List<Weather> allWeatherForRegion = this.findById(id);
        LocalDateTime now = LocalDateTime.now();
        return allWeatherForRegion.stream()
                .filter(x -> (x.getDateTime().toLocalDate().equals(LocalDate.now())))
                .toList();
    }
}
