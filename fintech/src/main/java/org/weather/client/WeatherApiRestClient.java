package org.weather.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.weather.dto.WeatherApiDTO;
import org.weather.exception.weatherapi.WeatherApiDisabledKeyException;
import org.weather.exception.weatherapi.WeatherApiIncorrectQueryException;
import org.weather.exception.weatherapi.WeatherApiUnknownException;
import org.weather.exception.weatherapi.WeatherApiWrongKeyException;
import org.weather.service.impl.DefaultWeatherControllerService;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class WeatherApiRestClient {
    private final DefaultWeatherControllerService weatherControllerService;
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final String weatherApiKey = "57ca5d93a8584509b18115355230310";
    private final String weatherApiUrlPath = "https://api.weatherapi.com/v1/current.json";

    public double getTemperatureFromWeatherApi(String regionName) {
        String finalPath = weatherApiUrlPath + "?key=" + weatherApiKey + "&q=" + regionName + "&aqi=no";
        try {
            ResponseEntity<WeatherApiDTO> responseEntity = restTemplate.exchange(
                    finalPath,
                    HttpMethod.GET,
                    null,
                    WeatherApiDTO.class
            );
            return this.weatherControllerService.getTemperatureFromExternalApi(responseEntity.getBody());
        } catch (HttpStatusCodeException ex) {
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new WeatherApiIncorrectQueryException(HttpStatus.BAD_REQUEST, messageSource
                        .getMessage("weatherapi.invalid.url.message", null, Locale.getDefault()), 1);
            }
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new WeatherApiWrongKeyException(HttpStatus.UNAUTHORIZED, messageSource
                        .getMessage("weatherapi.wrong.key.message", null, Locale.getDefault()), 1);
            }
            if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new WeatherApiDisabledKeyException(HttpStatus.FORBIDDEN, messageSource
                        .getMessage("weatherapi.disabled.key.message", null, Locale.getDefault()), 1);
            }
        }
        throw new WeatherApiUnknownException(HttpStatus.REQUEST_TIMEOUT, messageSource
                .getMessage("weatherapi.unknown.exception.message", null, Locale.getDefault()), 1);
    }
}
