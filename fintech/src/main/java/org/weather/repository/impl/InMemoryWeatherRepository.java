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
    private final Map<String, UUID> RegionToIdMap;
    private final Map<UUID, List<Weather>> IdToWeatherMap;

    public InMemoryWeatherRepository() {
        RegionToIdMap = new HashMap<>();
        IdToWeatherMap = new HashMap<>();
    }

    @Override
    public Map<UUID, List<Weather>> findAll() {
        return IdToWeatherMap;
    }

    @Override
    public Optional<List<Weather>> findById(UUID id) {
        return Optional.ofNullable(this.IdToWeatherMap.get(id));
    }

    @Override
    public void saveRegion(String regionName) {
        UUID newId = UUID.randomUUID();
        while (IdToWeatherMap.containsKey(newId)) {
            newId = UUID.randomUUID();
        }
        IdToWeatherMap.put(newId, new ArrayList<>());
        RegionToIdMap.put(regionName.toLowerCase(), newId);
    }

    @Override
    public void saveWeather(String regionName, WeatherDTO payload) {
        if(!RegionToIdMap.containsKey(regionName.toLowerCase())) {
           this.saveRegion(regionName);
        }
        UUID curId = RegionToIdMap.get(regionName.toLowerCase());
        Weather newWeather = new Weather(curId, regionName.toLowerCase(),
                payload.getTemperatureValue(), payload.getDateTime());
        IdToWeatherMap.get(curId).add(newWeather);
    }

    @Override
    public UUID getIdByRegionName(String regionName) {
        return RegionToIdMap.get(regionName);
    }

    @Override
    public void updateWeatherWithSameRegionAndDate(UUID id, String regionName, WeatherDTO payload) {
        Weather newWeather = new Weather(id, regionName.toLowerCase(),
                payload.getTemperatureValue(), payload.getDateTime());
        List<Weather> newWeatherList = this.IdToWeatherMap.get(id).stream()
                .map(x -> (x.getDateTime().isEqual(payload.getDateTime())) ? newWeather : x)
                .collect(Collectors.toList());
        this.IdToWeatherMap.put(id, newWeatherList);
    }

    @Override
    public void deleteRegion(UUID currentId, String regionName) {
        this.IdToWeatherMap.remove(currentId);
        this.RegionToIdMap.remove(regionName);
    }

    @Override
    public boolean hasWeatherWithSameIdAndDate(UUID id, LocalDateTime dateTime) {
        return this.IdToWeatherMap.get(id).stream()
                .anyMatch(x -> x.getDateTime().isEqual(dateTime));
    }
}
