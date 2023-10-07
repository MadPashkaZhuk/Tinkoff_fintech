package org.weather.controller;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.weather.client.WeatherApiRestClient;
import org.weather.exception.weatherapi.WeatherApiTooManyRequestsException;

import java.util.Locale;

@Controller
@RequestMapping("/external/weatherapi")
public class WeatherApiController {
    private final WeatherApiRestClient weatherApiRestClient;
    private final MessageSource messageSource;
    private final int tooManyRequestsErrorCode;
    private final String tooManyRequestsMessage = "weatherapi.too.many.requests.message";

    public WeatherApiController(WeatherApiRestClient weatherApiRestClient,
                                MessageSource messageSource,
                                @Value("${weather.api.too.many.requests.code}") int tooManyRequestsErrorCode) {
        this.weatherApiRestClient = weatherApiRestClient;
        this.messageSource = messageSource;
        this.tooManyRequestsErrorCode = tooManyRequestsErrorCode;
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
    @RateLimiter(name = "weatherApiCurrent", fallbackMethod = "fallbackMethod")
    public ResponseEntity<?> handleGetTemperature(@PathVariable("regionName") String regionName) {
        return ResponseEntity.ok()
                .body(this.weatherApiRestClient.getTemperatureFromWeatherApi(regionName));
    }

    public ResponseEntity<?> fallbackMethod(RequestNotPermitted requestNotPermitted) {
        throw new WeatherApiTooManyRequestsException(HttpStatus.TOO_MANY_REQUESTS, messageSource
                .getMessage(tooManyRequestsMessage, null, Locale.getDefault()),
                tooManyRequestsErrorCode);
    }
}
