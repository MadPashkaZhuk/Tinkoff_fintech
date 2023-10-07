package org.weather.client;

import org.springframework.beans.factory.annotation.Qualifier;
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

    public WeatherApiRestClient(DefaultWeatherControllerService weatherControllerService,
                                @Qualifier("WeatherApiRestTemplate") RestTemplate restTemplate,
                                MessageSource messageSource,
                                @Value("${weather.api.key}") String weatherApiKey,
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
        ResponseEntity<WeatherApiDTO> responseEntity = null;
        String finalPath = UriComponentsBuilder.fromUriString(weatherApiUrlPath)
                .queryParam("key", weatherApiKey)
                .queryParam("q", regionName)
                .queryParam("aqi", "no")
                .toUriString();
        try {
            responseEntity = restTemplate.exchange(
                    finalPath,
                    HttpMethod.GET,
                    null,
                    WeatherApiDTO.class
            );
            WeatherApiDTO weatherApiDTO = responseEntity.getBody();
            return this.weatherControllerService.getTemperatureFromWeatherApi(weatherApiDTO);
        } catch (HttpStatusCodeException ex) {
            WeatherApiErrorDTO weatherApiErrorDTO = ex.getResponseBodyAs(WeatherApiErrorDTO.class);
            int errorCode = weatherApiErrorDTO.getError().getCode();
            if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new WeatherApiIncorrectQueryException(HttpStatus.BAD_REQUEST,
                        getMessageFromMessageSource(weatherApiInvalidUrlMessage), errorCode);
            }
            if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new WeatherApiWrongKeyException(HttpStatus.UNAUTHORIZED,
                        getMessageFromMessageSource(weatherApiWrongKeyMessage), errorCode);
            }
            if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new WeatherApiDisabledKeyException(HttpStatus.FORBIDDEN,
                        getMessageFromMessageSource(weatherApiDisabledKeyMessage), errorCode);
            }
            else {
                throw new WeatherApiUnknownException(HttpStatus.REQUEST_TIMEOUT,
                        getMessageFromMessageSource(weatherApiUnknownExceptionMessage), unknownErrorCode);
            }
        } catch (Throwable ex) {
            throw new WeatherApiUnknownException(HttpStatus.REQUEST_TIMEOUT,
                    getMessageFromMessageSource(weatherApiUnknownExceptionMessage), unknownErrorCode);
        }
    }

    private String getMessageFromMessageSource(String messageCode) {
        return messageSource.getMessage(messageCode, null, Locale.getDefault());
    }
}

