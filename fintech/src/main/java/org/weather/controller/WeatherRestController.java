package org.weather.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;
import org.weather.exception.WeatherAlreadyExistsException;
import org.weather.exception.WeatherNotFoundException;
import org.weather.repository.WeatherRepository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/weather")
public class WeatherRestController {
    private final WeatherRepository weatherRepository;
    private final String WEATHER_NOT_FOUND_MESSAGE = "Region with this name doesn't exist.";
    private final String WEATHER_ALREADY_EXISTS_MESSAGE = "Weather with same region and date already exists. " +
            "If you need to update it, use Put method";

    public WeatherRestController(WeatherRepository weatherRepository) {
        this.weatherRepository = weatherRepository;
    }

    @GetMapping
    public ResponseEntity<Map<UUID, List<Weather>>> getAllWeather() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherRepository.findAll());
    }

    @GetMapping("/{regionName}")
    public ResponseEntity<List<Weather>> getWeatherById(@PathVariable("regionName") String regionName) {
        UUID currentId = this.weatherRepository.getIdByRegionName(regionName.toLowerCase());
        if(currentId == null) {
            throw new WeatherNotFoundException(WEATHER_NOT_FOUND_MESSAGE);
        }
        return ResponseEntity.of(this.weatherRepository
                .findById(currentId));
    }

    @PostMapping("/{regionName}")
    public ResponseEntity<?> createNewWeather(@PathVariable("regionName") String regionName,
                                              @RequestBody @Valid WeatherDTO payload,
                                              UriComponentsBuilder uriComponentsBuilder) {
        if(this.weatherRepository.getIdByRegionName(regionName) != null &&
                this.weatherRepository.hasWeatherWithSameIdAndDate
                        (this.weatherRepository.getIdByRegionName(regionName), payload.getDateTime())) {
            throw new WeatherAlreadyExistsException(WEATHER_ALREADY_EXISTS_MESSAGE);
        }
        this.weatherRepository.saveWeather(regionName, payload);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/weather/{regionName}")
                        .build(Map.of("regionName", regionName.toLowerCase())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(weatherRepository
                                .findById(weatherRepository.getIdByRegionName(regionName.toLowerCase())));
    }

    @PutMapping("/{regionName}")
    public ResponseEntity<?> updateWeatherTemperature(@PathVariable("regionName") String regionName,
                                                      @RequestBody WeatherDTO payload,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        UUID currentId = this.weatherRepository.getIdByRegionName(regionName.toLowerCase());
        if(currentId == null) {
            throw new WeatherNotFoundException(WEATHER_NOT_FOUND_MESSAGE);
        }

        if(this.weatherRepository.hasWeatherWithSameIdAndDate(currentId, payload.getDateTime())) {
            this.weatherRepository.updateWeatherWithSameRegionAndDate(currentId, regionName, payload);
        }
        else {
            this.weatherRepository.saveWeather(regionName, payload);
        }

        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/weather/{regionName}")
                        .build(Map.of("regionName", regionName.toLowerCase())))
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherRepository.findById(currentId));
    }

    @DeleteMapping("/{regionName}")
    public ResponseEntity<?> handleDeleteRegion(@PathVariable("regionName") String regionName) {
        UUID currentId = this.weatherRepository.getIdByRegionName(regionName.toLowerCase());
        if(currentId == null) {
            throw new WeatherNotFoundException(WEATHER_NOT_FOUND_MESSAGE);
        }

        this.weatherRepository.deleteRegion(currentId, regionName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherRepository.findAll());
    }
}
