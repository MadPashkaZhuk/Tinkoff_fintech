package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.NewWeatherDTO;
import org.weather.dto.WeatherDTO;
import org.weather.service.WeatherService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weather")
public class WeatherRestController {
    private final WeatherService weatherService;

    public WeatherRestController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(summary = "Get all weather in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All data retrieved successfully")
    })
    @GetMapping
    ResponseEntity<List<WeatherDTO>> getAllWeather() {
        return ResponseEntity.ok()
                .body(weatherService.findAll());
    }

    @Operation(summary = "Get all weather info by city's name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weather data retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "City with this name doesn't exist in database")
    })
    @GetMapping("/{cityName}")
    ResponseEntity<?> getAllWeatherForCity(@PathVariable("cityName") String cityName) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherService.getWeatherForCity(cityName));
    }

    @Operation(summary = "Add new weather data for specific city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New weather data added successfully"),
            @ApiResponse(responseCode = "400", description = "Weather with this date and city already exists"),
            @ApiResponse(responseCode = "404", description = "Handbook with this id doesn't exist in database"),
            @ApiResponse(responseCode = "404", description = "City with this name doesn't exist in database")
    })
    @PostMapping("/{cityName}")
    ResponseEntity<?> saveWeatherForCity(@PathVariable("cityName") String cityName,
                                         @RequestBody NewWeatherDTO newWeatherDTO,
                                         UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/weather/{cityName}")
                        .build(Map.of("cityName", cityName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherService.saveWeatherForCity(cityName, newWeatherDTO));
    }

    @Operation(summary = "Delete weather info by specific dateTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Weather data deleted successfully")
    })
    @DeleteMapping("/{cityName}")
    ResponseEntity<?> deleteWeatherForCityByDateTime(@PathVariable("cityName") String cityName,
                                                     @RequestBody NewWeatherDTO newWeatherDTO) {
        weatherService.deleteWeatherByDateTime(cityName, newWeatherDTO);
        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "Delete weather info by specific dateTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Weather data updated successfully")
    })
    @PutMapping("/{cityName}")
    ResponseEntity<?> updateWeatherForCityByDateTime(@PathVariable("cityName") String cityName,
                                                     @RequestBody NewWeatherDTO newWeatherDTO,
                                                     UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/weather/{cityName}")
                        .build(Map.of("cityName", cityName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherService.updateWeatherForCity(cityName, newWeatherDTO));
    }
}
