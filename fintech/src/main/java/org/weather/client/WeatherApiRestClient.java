package org.weather.client;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.weatherapi.WeatherApiDTO;
import org.weather.exception.weatherapi.*;
import org.weather.utils.WeatherApiMapper;
import org.weather.utils.ErrorCodeHelper;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.ErrorCodeEnum;
import org.weather.utils.enums.WeatherMessageEnum;

@Component
public class WeatherApiRestClient {
    private final WeatherApiMapper weatherApiMapper;
    private final RestTemplate restTemplate;
    private final MessageSourceWrapper messageSource;
    private final ErrorCodeHelper errorCodeHelper;
    private final ClientProperties clientProperties;

    public WeatherApiRestClient(WeatherApiMapper weatherApiMapper,
                                @Qualifier("WeatherApiRestTemplate") RestTemplate restTemplate,
                                MessageSourceWrapper messageSource,
                                ErrorCodeHelper errorCodeHelper,
                                ClientProperties clientProperties) {
        this.weatherApiMapper = weatherApiMapper;
        this.restTemplate = restTemplate;
        this.messageSource = messageSource;
        this.errorCodeHelper = errorCodeHelper;
        this.clientProperties = clientProperties;
    }

    @RateLimiter(name = "weatherApiCurrent")
    public double getTemperatureFromWeatherApi(String regionName) {
        ResponseEntity<WeatherApiDTO> responseEntity;
        String finalPath = UriComponentsBuilder.fromUriString(clientProperties.getUrl())
                .queryParam("key", clientProperties.getKey())
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
            return weatherApiMapper.getTemperatureFromWeatherApi(weatherApiDTO);
        } catch (HttpStatusCodeException ex) {
            throw getWeatherApiExceptionFromHttpException(ex);
        } catch (Throwable ex) {
            throw new WeatherApiUnknownException(HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessageCode(WeatherMessageEnum.UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE));
        }
    }

    @RateLimiter(name = "weatherApiCurrent")
    public WeatherApiDTO getDTOFromWeatherApi(String regionName) {
        ResponseEntity<WeatherApiDTO> responseEntity;
        String finalPath = UriComponentsBuilder.fromUriString(clientProperties.getUrl())
                .queryParam("key", clientProperties.getKey())
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
            return responseEntity.getBody();
        } catch (HttpStatusCodeException ex) {
            throw getWeatherApiExceptionFromHttpException(ex);
        } catch (Throwable ex) {
            throw new WeatherApiUnknownException(HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessageCode(WeatherMessageEnum.UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE));
        }
    }

    private BaseWeatherApiException getWeatherApiExceptionFromHttpException(HttpStatusCodeException ex) {
        BaseWeatherApiException exception;
        if(ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
            exception = new WeatherApiIncorrectQueryException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessageCode(WeatherMessageEnum.INVALID_URL),
                    errorCodeHelper.getCode(ErrorCodeEnum.INVALID_URL));
        } else if(ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            exception = new WeatherApiWrongKeyException(HttpStatus.UNAUTHORIZED,
                    messageSource.getMessageCode(WeatherMessageEnum.API_KEY_INVALID),
                    errorCodeHelper.getCode(ErrorCodeEnum.API_KEY_INVALID));
        } else if(ex.getStatusCode() == HttpStatus.FORBIDDEN) {
            exception = new WeatherApiDisabledKeyException(HttpStatus.FORBIDDEN,
                    messageSource.getMessageCode(WeatherMessageEnum.API_KEY_DISABLED),
                    errorCodeHelper.getCode(ErrorCodeEnum.API_KEY_DISABLED));
        } else {
            exception = new WeatherApiUnknownException(HttpStatus.INTERNAL_SERVER_ERROR,
                    messageSource.getMessageCode(WeatherMessageEnum.UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE));
        }
        return exception;
    }
}
