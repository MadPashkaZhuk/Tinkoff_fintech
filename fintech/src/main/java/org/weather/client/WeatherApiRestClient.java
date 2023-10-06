package org.weather.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.WeatherApiDTO;
import org.weather.dto.WeatherApiErrorDTO;
import org.weather.exception.weatherapi.WeatherApiDisabledKeyException;
import org.weather.exception.weatherapi.WeatherApiIncorrectQueryException;
import org.weather.exception.weatherapi.WeatherApiUnknownException;
import org.weather.exception.weatherapi.WeatherApiWrongKeyException;
import org.weather.service.impl.DefaultWeatherControllerService;

import java.util.Locale;

@Component
public class WeatherApiRestClient {
    private final DefaultWeatherControllerService weatherControllerService;
    private final String weatherApiInvalidUrlMessage = "weatherapi.invalid.url.message";
    private final String weatherApiWrongKeyMessage = "weatherapi.wrong.key.message";
    private final String weatherApiDisabledKeyMessage = "weatherapi.disabled.key.message";
    private final String weatherApiUnknownExceptionMessage = "weatherapi.unknown.exception.message";
    private final int unknownErrorCode;
    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final String weatherApiKey;
    private final String weatherApiUrlPath;

    public WeatherApiRestClient(DefaultWeatherControllerService weatherControllerService, RestTemplate restTemplate,
                                MessageSource messageSource, @Value("${weather.api.key}") String weatherApiKey,
                                @Value("${weather.api.url}") String weatherApiUrlPath,
                                @Value("${weather.api.unknown.error.code}") int unknownErrorCode) {
        this.weatherControllerService = weatherControllerService;
        this.restTemplate = restTemplate;
        this.messageSource = messageSource;
        this.weatherApiKey = weatherApiKey;
        this.weatherApiUrlPath = weatherApiUrlPath;
        this.unknownErrorCode = unknownErrorCode;
    }

    public double getTemperatureFromWeatherApi(String regionName) {
        String finalPath = UriComponentsBuilder.fromUriString(weatherApiUrlPath)
                .queryParam("key", weatherApiKey)
                .queryParam("q", regionName)
                .queryParam("aqi", "no")
                .toUriString();
        try {
            ResponseEntity<WeatherApiDTO> responseEntity = restTemplate.exchange(
                    finalPath,
                    HttpMethod.GET,
                    null,
                    WeatherApiDTO.class
            );
            return this.weatherControllerService.getTemperatureFromExternalApi(responseEntity.getBody());
        } catch (HttpStatusCodeException ex) {
            WeatherApiErrorDTO weatherApiErrorDTO = ex.getResponseBodyAs(WeatherApiErrorDTO.class);
            int errorCode = weatherApiErrorDTO.getError().getCode();
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new WeatherApiIncorrectQueryException(HttpStatus.BAD_REQUEST, messageSource
                        .getMessage(weatherApiInvalidUrlMessage, null, Locale.getDefault()), errorCode);
            }
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new WeatherApiWrongKeyException(HttpStatus.UNAUTHORIZED, messageSource
                        .getMessage(weatherApiWrongKeyMessage, null, Locale.getDefault()), errorCode);
            }
            if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new WeatherApiDisabledKeyException(HttpStatus.FORBIDDEN, messageSource
                        .getMessage(weatherApiDisabledKeyMessage, null, Locale.getDefault()), errorCode);
            }
        }
        throw new WeatherApiUnknownException(HttpStatus.REQUEST_TIMEOUT, messageSource
                .getMessage(weatherApiUnknownExceptionMessage, null, Locale.getDefault()), unknownErrorCode);
    }
}