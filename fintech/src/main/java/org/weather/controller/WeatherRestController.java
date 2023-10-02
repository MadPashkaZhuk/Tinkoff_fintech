package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Weather;
import org.weather.exception.BaseWeatherException;
import org.weather.service.WeatherControllerService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherRestController {
    private final WeatherControllerService weatherService;
    private final static String PAGE_NOT_FOUND_MESSAGE = "Page you are trying to get doesn't exist.";

    @Operation(summary = "Show all weather info",
            description = "Show all weather info for all regions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All information is shown")
    })
    @GetMapping
    public ResponseEntity<Map<UUID, List<Weather>>> getAllWeather() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherService.findAll());
    }

    @GetMapping("/{regionName}")
    @Operation(summary = "Get all weather by region name and current date",
            description = "Get list of weather for a specific region by name and current date.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weather data retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Region doesn't exist in database")
    })
    public ResponseEntity<List<Weather>> getWeatherById(@PathVariable("regionName") String regionName) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherService.findWeatherListByRegionAndCurrentDay(regionName));
    }

    @Operation(summary = "Add new weather info to region",
            description = "Add new region and/or add weather info to this region.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New weather data created successfully"),
            @ApiResponse(responseCode = "400", description = "Weather info with this region and date already exists." +
                    "If you want to update it, use Put method")
    })
    @PostMapping("/{regionName}")
    public ResponseEntity<?> createNewWeather(@PathVariable("regionName") String regionName,
                                              @RequestBody @Valid WeatherDTO newWeatherDTO,
                                              UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/weather/{regionName}")
                        .build(Map.of("regionName", regionName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherService.createNewWeather(regionName, newWeatherDTO));
    }

    @Operation(summary = "Update weather info for region",
            description = "Update weather info for region. If no date matches," +
                    " then adding new weather data to this region.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Weather data updated successfully"),
            @ApiResponse(responseCode = "404", description = "Region doesn't exist in database")
    })
    @PutMapping("/{regionName}")
    public ResponseEntity<?> updateWeatherTemperature(@PathVariable("regionName") String regionName,
                                                      @RequestBody WeatherDTO newWeatherDTO,
                                                      UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/weather/{regionName}")
                        .build(Map.of("regionName", regionName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(this.weatherService.updateWeatherTemperature(regionName, newWeatherDTO));
    }

    @Operation(summary = "Delete region in database",
            description = "Delete all region info and region itself.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Weather deleted successfully"),
    })
    @DeleteMapping("/{regionName}")
    public ResponseEntity<?> handleDeleteRegion(@PathVariable("regionName") String regionName) {
        this.weatherService.deleteRegion(regionName);
        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "Grab all wrong urls",
            description = "Process situations, when users tries to access non-existing page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "This page doesn't exist")
    })
    @RequestMapping(value = "/{text}/**")
    public ResponseEntity<?> handleNonExistingPage() {
        throw new BaseWeatherException(404, PAGE_NOT_FOUND_MESSAGE);
    }
}
