package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.entity.City;
import org.weather.service.hibernate.CityService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cities")
public class CityRestController {
    private final CityService cityService;

    @Operation(summary = "Show all city info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All information is shown")
    })
    @GetMapping
    public ResponseEntity<List<City>> getCities() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(cityService.findAll());
    }

    @Operation(summary = "Get city by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "City data retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "City with this id doesn't exist in database")
    })
    @GetMapping("/{id}")
    public ResponseEntity<City> getCityById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(cityService.findCityById(id));
    }

    @Operation(summary = "Add new city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New city created successfully"),
            @ApiResponse(responseCode = "400", description = "City with this name already created")
    })
    @PostMapping("/{cityName}")
    public ResponseEntity<City> saveCity(@PathVariable("cityName") String cityName,
                                         UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/city/{cityName}")
                        .build(Map.of("cityName", cityName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(cityService.save(cityName));
    }

    @Operation(summary = "Delete city in database",
            description = "Delete all city and weather connected to this city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "City deleted successfully")
    })
    @DeleteMapping("/{cityName}")
    public ResponseEntity<?> deleteCity(@PathVariable("cityName") String cityName) {
        this.cityService.deleteCity(cityName);
        return ResponseEntity.noContent()
                .build();
    }
}
