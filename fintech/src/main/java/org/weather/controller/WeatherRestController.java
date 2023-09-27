package org.weather.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;
import org.weather.exception.CustomPageNotFoundException;
import org.weather.exception.WeatherNotFoundException;
import org.weather.service.WeatherControllerService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/weather")
public class WeatherRestController {
    private final WeatherControllerService weatherService;
    private final String WEATHER_NOT_FOUND_MESSAGE = "Region with this name doesn't exist.";
    private final String PAGE_NOT_FOUND_MESSAGE = "Page you are trying to get doesn't exist.";

    public WeatherRestController(WeatherControllerService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<Map<UUID, List<Weather>>> getAllWeather() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherService.findAll());
    }

    @GetMapping("/{regionName}")
    public ResponseEntity<List<Weather>> getWeatherById(@PathVariable("regionName") String regionName) {
        UUID currentId = this.weatherService.getIdByRegionName(regionName);
        if(currentId == null) {
            throw new WeatherNotFoundException(WEATHER_NOT_FOUND_MESSAGE);
        }
        return ResponseEntity.of(this.weatherService
                .findById(currentId));
    }

    @PostMapping("/{regionName}")
    public ResponseEntity<?> createNewWeather(@PathVariable("regionName") String regionName,
                                              @RequestBody @Valid WeatherDTO newWeatherDTO,
                                              UriComponentsBuilder uriComponentsBuilder) {
        this.weatherService.createNewWeather(regionName, newWeatherDTO);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/weather/{regionName}")
                        .build(Map.of("regionName", regionName)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(weatherService
                                .findById(weatherService.getIdByRegionName(regionName)));
    }

    @PutMapping("/{regionName}")
    public ResponseEntity<?> updateWeatherTemperature(@PathVariable("regionName") String regionName,
                                                      @RequestBody WeatherDTO newWeatherDTO,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        UUID currentId = this.weatherService.getIdByRegionName(regionName);
        if(currentId == null) {
            throw new WeatherNotFoundException(WEATHER_NOT_FOUND_MESSAGE);
        }
        this.weatherService.updateWeatherTemperature(currentId, regionName, newWeatherDTO);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/weather/{regionName}")
                        .build(Map.of("regionName", regionName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherService.findById(currentId));
    }

    @DeleteMapping("/{regionName}")
    public ResponseEntity<?> handleDeleteRegion(@PathVariable("regionName") String regionName) {
        UUID currentId = this.weatherService.getIdByRegionName(regionName);
        if(currentId == null) {
            throw new WeatherNotFoundException(WEATHER_NOT_FOUND_MESSAGE);
        }
        this.weatherService.deleteRegion(currentId, regionName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherService.findAll());
    }

    @GetMapping(value = "/{text}/**")
    public ResponseEntity<?> handleNonExistingPage() {
        throw new CustomPageNotFoundException(PAGE_NOT_FOUND_MESSAGE);
    }
}
