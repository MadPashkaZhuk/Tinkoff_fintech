package org.weather.client;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.WeatherApiDTO;
import org.weather.dto.WeatherApiErrorDTO;
import org.weather.exception.weatherapi.*;
import org.weather.service.impl.DefaultWeatherControllerService;
import org.weather.utils.ErrorCodeHelper;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.ErrorCodeEnum;
import org.weather.utils.enums.WeatherMessageEnum;

@Component
@Setter
@ConfigurationProperties(prefix = "weather.api")
public class WeatherApiRestClient {
    private final DefaultWeatherControllerService weatherControllerService;
    private final RestTemplate restTemplate;
    private final MessageSourceWrapper messageSource;
    private final ErrorCodeHelper errorCodeHelper;
    private String key;
    private String url;

    public WeatherApiRestClient(DefaultWeatherControllerService weatherControllerService,
                                @Qualifier("WeatherApiRestTemplate") RestTemplate restTemplate,
                                MessageSourceWrapper messageSource,
                                ErrorCodeHelper errorCodeHelper) {
        this.weatherControllerService = weatherControllerService;
        this.restTemplate = restTemplate;
        this.messageSource = messageSource;
        this.errorCodeHelper = errorCodeHelper;
    }

    @RateLimiter(name = "weatherApiCurrent")
    public double getTemperatureFromWeatherApi(String regionName) {
        ResponseEntity<WeatherApiDTO> responseEntity;
        String finalPath = UriComponentsBuilder.fromUriString(url)
                .queryParam("key", key)
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
            throw getWeatherApiExceptionFromHttpException(ex);
        } catch (Throwable ex) {
            throw new WeatherApiUnknownException(HttpStatus.REQUEST_TIMEOUT,
                    messageSource.getMessageCode(WeatherMessageEnum.UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE));
        }
    }

    private BaseWeatherApiException getWeatherApiExceptionFromHttpException(HttpStatusCodeException ex) {
        WeatherApiErrorDTO weatherApiErrorDTO = ex.getResponseBodyAs(WeatherApiErrorDTO.class);
        int errorCode = weatherApiErrorDTO.getError().getCode();
        BaseWeatherApiException exception;
        if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            exception = new WeatherApiIncorrectQueryException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessageCode(WeatherMessageEnum.INVALID_URL), errorCode);
        } else if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            exception = new WeatherApiWrongKeyException(HttpStatus.UNAUTHORIZED,
                    messageSource.getMessageCode(WeatherMessageEnum.API_KEY_INVALID), errorCode);
        } else if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
            exception = new WeatherApiDisabledKeyException(HttpStatus.FORBIDDEN,
                    messageSource.getMessageCode(WeatherMessageEnum.API_KEY_DISABLED), errorCode);
        } else {
            exception = new WeatherApiUnknownException(HttpStatus.REQUEST_TIMEOUT,
                    messageSource.getMessageCode(WeatherMessageEnum.UNKNOWN_EXCEPTION), errorCode);
        }
        return exception;
    }
}

