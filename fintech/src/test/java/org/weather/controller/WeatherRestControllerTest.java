package org.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.WeatherDTO;
import org.weather.exception.weather.WeatherNotFoundException;
import org.weather.service.WeatherService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherRestController.class)
public class WeatherRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    WeatherService weatherService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getAllWeather_ShouldReturnAllWeather_WhenDataExists() throws Exception{
        WeatherDTO weatherDTO1 = generateWeatherDTO(
                10.1,
                LocalDateTime.of(2023, 11, 5, 18, 45),
                "Minsk",
                generateHandbookDTO(1, "Raining"));
        WeatherDTO weatherDTO2 = generateWeatherDTO(
                20,
                LocalDateTime.of(2023, 11, 5, 19, 0),
                "Brest",
                generateHandbookDTO(2, "Sunshine"));
        when(weatherService.findAll()).thenReturn(List.of(weatherDTO1, weatherDTO2));
        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(weatherDTO1, weatherDTO2))));
    }

    @Test
    public void getAllWeather_ShouldReturnEmptyWeatherList_WhenNoData() throws Exception {
        when(weatherService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/weather"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getAllWeatherForCity_ShouldReturnWeatherList_WhenCityExists() throws Exception {
        WeatherDTO weatherDTO1 = generateWeatherDTO(
                10.1,
                LocalDateTime.of(2023, 11, 5, 18, 45),
                "Minsk",
                generateHandbookDTO(1, "Raining"));
        when(weatherService.getWeatherForCity("Minsk")).thenReturn(List.of(weatherDTO1));
        mockMvc.perform(get("/weather/Minsk"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(weatherDTO1))));
    }

    @Test
    public void getAllWeatherForCity_ShouldReturnEmptyWeatherList_WhenCityExistsAndNoData() throws Exception {
        when(weatherService.getWeatherForCity("Minsk")).thenReturn(List.of());
        mockMvc.perform(get("/weather/Minsk"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }

    @Test
    public void getAllWeatherForCity_ShouldThrowNotFound_WhenCityDoesntExist() throws Exception {
        when(weatherService.getWeatherForCity("Test"))
                .thenThrow(new WeatherNotFoundException(HttpStatus.NOT_FOUND, "NOT_FOUND"));
        mockMvc.perform(get("/weather/Test"))
                .andExpect(status().isNotFound());
    }

    public WeatherDTO generateWeatherDTO(double temperature, LocalDateTime localDateTime,
                                         String cityName, HandbookDTO handbookDTO) {
        return new WeatherDTO(UUID.randomUUID(), temperature, localDateTime,
                generateCityDTO(cityName), handbookDTO);
    }

    public CityDTO generateCityDTO(String cityName) {
        return new CityDTO(UUID.randomUUID(), cityName);
    }

    public HandbookDTO generateHandbookDTO(int id, String typeName) {
        return new HandbookDTO(id, typeName);
    }
}
