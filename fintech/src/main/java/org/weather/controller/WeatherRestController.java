package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.WeatherDTO;
import org.weather.entity.WeatherEntity;
import org.weather.service.WeatherService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherRestController {
    private final WeatherService weatherServiceImpl;

    @Operation(summary = "Get all weather in database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All data retrieved successfully")
    })
    @GetMapping
    ResponseEntity<List<WeatherEntity>> getAllWeather() {
        return ResponseEntity.ok()
                .body(weatherServiceImpl.findAll());
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
                .body(weatherServiceImpl.getWeatherForCity(cityName));
    }

    @Operation(summary = "Add new weather data for specific city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New weather data added successfully"),
            @ApiResponse(responseCode = "404", description = "City with this name doesn't exist in database")
    })
    @PostMapping("/{cityName}")
    ResponseEntity<?> saveWeatherForCity(@PathVariable("cityName") String cityName,
                                         @RequestBody WeatherDTO weatherDTO,
                                         UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/weather/{cityName}")
                        .build(Map.of("cityName", cityName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherServiceImpl.saveWeatherForCity(cityName, weatherDTO));
    }

    @Operation(summary = "Delete weather info by specific dateTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Weather data deleted successfully")
    })
    @DeleteMapping("/{cityName}")
    ResponseEntity<?> deleteWeatherForCityByDateTime(@PathVariable("cityName") String cityName,
                                                     @RequestBody WeatherDTO weatherDTO) {
        weatherServiceImpl.deleteWeatherByDateTime(cityName, weatherDTO);
        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "Delete weather info by specific dateTime")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Weather data updated successfully"),
            @ApiResponse(responseCode = "404", description = "City provided doesn't exist")
    })
    @PutMapping("/{cityName}")
    ResponseEntity<?> updateWeatherForCityByDateTime(@PathVariable("cityName") String cityName,
                                                     @RequestBody WeatherDTO weatherDTO,
                                                     UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/weather/{cityName}")
                        .build(Map.of("cityName", cityName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(weatherServiceImpl.updateWeatherForCity(cityName, weatherDTO));
    }
}
