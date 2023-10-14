package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.CityDTO;
import org.weather.entity.City;
import org.weather.service.CityService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
public class CityRestController {
    private final CityService cityServiceImpl;

    @Operation(summary = "Show all city info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All information is shown")
    })
    @GetMapping
    public ResponseEntity<List<City>> getCities() {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(cityServiceImpl.findAll());
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
                .body(cityServiceImpl.findCityById(id));
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
                .body(cityServiceImpl.save(cityName));
    }

    @Operation(summary = "Update city in database",
            description = "Update city name in database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "City updated successfully"),
            @ApiResponse(responseCode = "404", description = "City provided doesn't exist")
    })
    @PutMapping("/{cityName}")
    public ResponseEntity<City> updateCity(@PathVariable("cityName") String cityName,
                                           @RequestBody CityDTO cityDTO,
                                           UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                    .path("/city/{cityName}")
                    .build(Map.of("cityName", cityName)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(cityServiceImpl.update(cityName, cityDTO));

    }
    @Operation(summary = "Delete city in database",
            description = "Delete all city and weather connected to this city.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "City deleted successfully")
    })
    @DeleteMapping("/{cityName}")
    public ResponseEntity<?> deleteCity(@PathVariable("cityName") String cityName) {
        this.cityServiceImpl.delete(cityName);
        return ResponseEntity.noContent()
                .build();
    }
}
