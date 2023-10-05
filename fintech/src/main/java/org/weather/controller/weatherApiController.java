package org.weather.controller;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.weather.client.WeatherApiRestClient;
import org.weather.exception.weatherapi.WeatherApiTooManyRequests;

import java.util.Locale;

@Controller
@RequiredArgsConstructor
@RequestMapping("/external/weatherapi")
public class weatherApiController {
    private final WeatherApiRestClient weatherApiRestClient;
    private final MessageSource messageSource;

    @Operation(summary = "Get temperature from weatherapi.com",
            description = "Get temperature from weatherapi.com by region name provided")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Temperature is shown successfully"),
    })
    @GetMapping("/{regionName}")
    @RateLimiter(name = "weatherApiCurrent", fallbackMethod = "fallbackMethod")
    public ResponseEntity<?> handleExternalGetTemperature(@PathVariable("regionName") String regionName) {
        return ResponseEntity.ok()
                .body(this.weatherApiRestClient.getTemperatureFromWeatherApi(regionName));
    }

    public ResponseEntity<?> fallbackMethod(RequestNotPermitted requestNotPermitted) {
        throw new WeatherApiTooManyRequests(HttpStatus.TOO_MANY_REQUESTS, messageSource
                .getMessage("weatherapi.too.many.requests.message", null, Locale.getDefault()), 7777);
    }
}
