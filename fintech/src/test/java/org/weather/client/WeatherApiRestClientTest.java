package org.weather.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.weatherapi.WeatherApiDTO;
import org.weather.exception.weatherapi.WeatherApiDisabledKeyException;
import org.weather.exception.weatherapi.WeatherApiIncorrectQueryException;
import org.weather.exception.weatherapi.WeatherApiUnknownException;
import org.weather.exception.weatherapi.WeatherApiWrongKeyException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest
public class WeatherApiRestClientTest {
    @Autowired
    @Qualifier("WeatherApiRestTemplate")
    RestTemplate restTemplate;
    @Autowired
    private WeatherApiRestClient weatherApiRestClient;
    @Autowired
    ClientProperties clientProperties;
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void getDTOFromWeatherApi_ShouldReturnDTO_WhenHappyPath() {
        final String regionName = "Minsk";
        final String localTime = "2023-10-28 14:30";
        final String handbookType = "Fog";
        final String temperature = "7.0";
        WeatherApiDTO actualDTO = getWeatherApiDTO(regionName, localTime, Double.parseDouble(temperature), handbookType);
        mockServer.expect(requestTo(getDefaultUriForRegionName(regionName)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getDefaultResponseFromWeatherApi(regionName, temperature, handbookType, localTime)));
        WeatherApiDTO response = weatherApiRestClient.getDTOFromWeatherApi(regionName);
        mockServer.verify();
        assertEquals(response, actualDTO);
    }

    @Test
    void getDTOFromWeatherApi_ShouldThrowWeatherApiIncorrectQueryException_WhenNoMatchingLocation() {
        final String regionName = "TEST";
        final String actualMessage = "Your url is invalid, please check again.";
        final String weatherApiMessage = "No matching location found.";
        final String weatherApiErrorCode = "2222";
        mockServer.expect(requestTo(getDefaultUriForRegionName(regionName)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getDefaultErrorFromWeatherApi(weatherApiErrorCode, weatherApiMessage)));
        Throwable exception = assertThrows(WeatherApiIncorrectQueryException.class,
                () -> weatherApiRestClient.getDTOFromWeatherApi(regionName));
        mockServer.verify();
        assertEquals(exception.getMessage(), actualMessage);
    }

    @Test
    void getDTOFromWeatherApi_ShouldThrowWeatherApiDisabledKeyException_WhenKeyProvidedIsDisabled() {
        final String regionName = "Minsk";
        final String actualMessage = "API KEY is disabled, check weatherapi.com for more info.";
        final String weatherApiMessage = "API key has been disabled.";
        final String weatherApiErrorCode = "2008";
        mockServer.expect(requestTo(getDefaultUriForRegionName(regionName)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getDefaultErrorFromWeatherApi(weatherApiErrorCode, weatherApiMessage)));
        Throwable exception = assertThrows(WeatherApiDisabledKeyException.class,
                () -> weatherApiRestClient.getDTOFromWeatherApi(regionName));
        mockServer.verify();
        assertEquals(exception.getMessage(), actualMessage);
    }

    @Test
    void getDTOFromWeatherApi_ShouldThrowWeatherApiUnknownException_WhenWeatherApiIsDown() {
        final String regionName = "Minsk";
        final String actualMessage = "Something went wrong with weatherapi.com. We are investigating.";
        mockServer.expect(requestTo(getDefaultUriForRegionName(regionName)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_JSON));
        Throwable exception = assertThrows(WeatherApiUnknownException.class,
                () -> weatherApiRestClient.getDTOFromWeatherApi(regionName));
        mockServer.verify();
        assertEquals(exception.getMessage(), actualMessage);
    }

    @Test
    void getDTOFromWeatherApi_ShouldThrowWeatherApiInvalidKey_WhenApiKeyInvalid() {
        final String regionName = "Minsk";
        final String actualMessage = "API KEY is not provided/invalid.";
        mockServer.expect(requestTo(getDefaultUriForRegionName(regionName)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.APPLICATION_JSON));
        Throwable exception = assertThrows(WeatherApiWrongKeyException.class,
                () -> weatherApiRestClient.getDTOFromWeatherApi(regionName));
        mockServer.verify();
        assertEquals(exception.getMessage(), actualMessage);
    }

    @Test
    void getTemperatureFromWeatherApi_ShouldReturnTemperature_WhenHappyPath() {
        final String regionName = "Minsk";
        final String localTime = "2023-10-28 14:30";
        final String handbookType = "Fog";
        final String temperature = "7.0";
        Double actualTemp = 7D;
        mockServer.expect(requestTo(getDefaultUriForRegionName(regionName)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(getDefaultResponseFromWeatherApi(regionName, temperature, handbookType, localTime)));
        Double response = weatherApiRestClient.getTemperatureFromWeatherApi(regionName);
        mockServer.verify();
        assertEquals(response, actualTemp);
    }

    private String getDefaultErrorFromWeatherApi(String errorCode, String message) {
        return String.format("{\"error\":{\"code\":%s,\"message\":\"%s\"}}", errorCode, message);
    }

    private WeatherApiDTO getWeatherApiDTO(String regionName, String dateTime, double temp, String condition) {
        return new WeatherApiDTO(
                new WeatherApiDTO.Location(regionName, dateTime),
                new WeatherApiDTO.Current(temp,
                        new WeatherApiDTO.Current.Condition(condition)),
                null
        );
    }

    private String getDefaultResponseFromWeatherApi(String regionName, String temp, String type, String localtime) {
        return String.format("{\"location\":{\"name\":\"%s\",\"region\":\"%s\",\"country\":\"Belarus\"" +
                        ",\"lat\":53.9,\"lon\":27.57,\"tz_id\":\"Europe/%s\",\"localtime_epoch\":1698429243" +
                        ",\"localtime\":\"%s\"},\"current\":{\"temp_c\":%s,\"condition\":{\"text\":\"%s\"}}}"
                , regionName, regionName, regionName, localtime, temp, type);
    }

    private String getDefaultUriForRegionName(String regionName) {
        return UriComponentsBuilder.fromUriString(clientProperties.getUrl())
                .queryParam("key", clientProperties.getKey())
                .queryParam("q", regionName)
                .queryParam("aqi", "no")
                .toUriString();
    }
}
