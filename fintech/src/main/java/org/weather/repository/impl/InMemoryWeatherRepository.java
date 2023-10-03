package org.weather.repository.impl;

import org.springframework.stereotype.Repository;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;
import org.weather.repository.WeatherRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryWeatherRepository implements WeatherRepository {
    private final Map<String, UUID> regionToIdMap;
    private final Map<UUID, List<Weather>> idToWeatherMap;

    public InMemoryWeatherRepository() {
        regionToIdMap = new HashMap<>();
        idToWeatherMap = new HashMap<>();
    }

    @Override
    public Map<String, List<Weather>> findAll() {
        Map<String, List<Weather>>  regionToWeatherMap = new HashMap<>();
        regionToIdMap.forEach( (k, v) -> regionToWeatherMap.put(k, idToWeatherMap.get(v)));
        return regionToWeatherMap;
    }

    @Override
    public List<Weather> findById(UUID id) {
        return new ArrayList<>(this.idToWeatherMap.get(id));
    }

    @Override
    public void saveRegion(String regionName) {
        UUID newId = UUID.randomUUID();
        while (idToWeatherMap.containsKey(newId)) {
            newId = UUID.randomUUID();
        }
        idToWeatherMap.put(newId, new ArrayList<>());
        regionToIdMap.put(regionName, newId);
    }

    @Override
    public void saveWeather(String regionName, WeatherDTO newWeatherDTO) {
        if(!regionToIdMap.containsKey(regionName)) {
           this.saveRegion(regionName);
        }
        UUID curId = regionToIdMap.get(regionName);
        Weather newWeather = new Weather(curId, regionName,
                newWeatherDTO.getTemperatureValue(), newWeatherDTO.getDateTime());
        idToWeatherMap.get(curId).add(newWeather);
    }

    @Override
    public UUID getIdByRegionName(String regionName) {
        return regionToIdMap.get(regionName);
    }

    @Override
    public void updateWeatherWithSameRegionAndDate(UUID id, String regionName, WeatherDTO newWeatherDTO) {
        Weather newWeather = new Weather(id, regionName,
                newWeatherDTO.getTemperatureValue(), newWeatherDTO.getDateTime());
        List<Weather> newWeatherList = this.idToWeatherMap.get(id).stream()
                .map(x -> (x.getDateTime().isEqual(newWeatherDTO.getDateTime())) ? newWeather : x)
                .collect(Collectors.toList());
        this.idToWeatherMap.put(id, newWeatherList);
    }

    @Override
    public void deleteRegion(UUID currentId, String regionName) {
        this.idToWeatherMap.remove(currentId);
        this.regionToIdMap.remove(regionName);
    }

    @Override
    public boolean hasWeatherWithSameIdAndDate(UUID id, LocalDateTime dateTime) {
        return this.idToWeatherMap.get(id).stream()
                .anyMatch(x -> x.getDateTime().isEqual(dateTime));
    }
}
