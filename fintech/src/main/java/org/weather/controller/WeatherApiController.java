package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.weather.client.WeatherApiRestClient;

@Controller
@RequestMapping("/api/external/weatherapi")
public class WeatherApiController {
    private final WeatherApiRestClient weatherApiRestClient;

    public WeatherApiController(WeatherApiRestClient weatherApiRestClient) {
        this.weatherApiRestClient = weatherApiRestClient;
    }

    @Operation(summary = "Get temperature from weatherapi.com",
            description = "Get temperature from weatherapi.com by region name provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temperature is shown successfully"),
            @ApiResponse(responseCode = "400", description = "Something wrong with url"),
            @ApiResponse(responseCode = "401", description = "Key provided is wrong"),
            @ApiResponse(responseCode = "403", description = "Key provided is disabled by weatherapi.com"),
            @ApiResponse(responseCode = "408", description = "Something wrong happened to weatherapi.com"),
            @ApiResponse(responseCode = "429", description = "Too many requests: Rate Limit is exceeded")
    })
    @GetMapping("/{regionName}")
    public ResponseEntity<?> handleGetTemperature(@PathVariable("regionName") String regionName) {
        return ResponseEntity.ok()
                .body(this.weatherApiRestClient.getTemperatureFromWeatherApi(regionName));
    }
}
