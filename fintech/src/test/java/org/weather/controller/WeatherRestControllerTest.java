package org.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.NewWeatherDTO;
import org.weather.dto.WeatherDTO;
import org.weather.exception.city.CityNotFoundException;
import org.weather.exception.handbook.HandbookTypeNotFoundException;
import org.weather.exception.weather.WeatherAlreadyExistsException;
import org.weather.exception.weather.WeatherNotFoundException;
import org.weather.service.WeatherService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherRestController.class)
@AutoConfigureMockMvc(addFilters = false)
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
        mockMvc.perform(get("/api/weather"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(weatherDTO1, weatherDTO2))));
    }

    @Test
    public void getAllWeather_ShouldReturnEmptyWeatherList_WhenNoData() throws Exception {
        when(weatherService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/weather"))
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
        mockMvc.perform(get("/api/weather/Minsk"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(weatherDTO1))));
    }

    @Test
    public void getAllWeatherForCity_ShouldReturnEmptyWeatherList_WhenCityExistsAndNoData() throws Exception {
        when(weatherService.getWeatherForCity("Minsk")).thenReturn(List.of());
        mockMvc.perform(get("/api/weather/Minsk"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of())));
    }

    @Test
    public void getAllWeatherForCity_ShouldThrowNotFound_WhenCityDoesntExist() throws Exception {
        when(weatherService.getWeatherForCity("Test"))
                .thenThrow(new WeatherNotFoundException(HttpStatus.NOT_FOUND, "NOT_FOUND"));
        mockMvc.perform(get("/api/weather/Test"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveWeather_ShouldReturnCreatedStatusAndWeatherDTO_WhenWeatherDoesntExist() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 5, 18, 45);
        NewWeatherDTO newWeatherDTO = generateNewWeatherDTO(15, localDateTime, 1);
        WeatherDTO weatherDTO = generateWeatherDTO(15,
                localDateTime,
                "Minsk",
                generateHandbookDTO(1, "Raining")
                );
        when(weatherService.saveWeatherForCity("Minsk", newWeatherDTO)).thenReturn(weatherDTO);
        mockMvc.perform(post("/api/weather/Minsk")
                        .content(objectMapper.writeValueAsString(newWeatherDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(weatherDTO)));
    }

    @Test
    public void saveWeather_ShouldThrowNotFound_WhenCityDoesntExist() throws Exception {
        NewWeatherDTO newWeatherDTO = generateNewWeatherDTO(15,
                LocalDateTime.of(2023, 11, 5, 18, 45),
                1);
        when(weatherService.saveWeatherForCity("Minsk", newWeatherDTO))
                .thenThrow(new CityNotFoundException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        mockMvc.perform(post("/api/weather/Minsk")
                        .content(objectMapper.writeValueAsString(newWeatherDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveWeather_ShouldThrowNotFound_WhenHandbookDoesntExist() throws Exception {
        NewWeatherDTO newWeatherDTO = generateNewWeatherDTO(15,
                LocalDateTime.of(2023, 11, 5, 18, 45),
                1);
        when(weatherService.saveWeatherForCity("Minsk", newWeatherDTO))
                .thenThrow(new HandbookTypeNotFoundException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        mockMvc.perform(post("/api/weather/Minsk")
                        .content(objectMapper.writeValueAsString(newWeatherDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveWeather_ShouldThrowBadRequest_WhenWeatherAlreadyExists() throws Exception {
        NewWeatherDTO newWeatherDTO = generateNewWeatherDTO(15,
                LocalDateTime.of(2023, 11, 5, 18, 45),
                1);
        when(weatherService.saveWeatherForCity("Minsk", newWeatherDTO))
                .thenThrow(new WeatherAlreadyExistsException(HttpStatus.BAD_REQUEST, "BAD_REQUEST"));
        mockMvc.perform(post("/api/weather/Minsk")
                        .content(objectMapper.writeValueAsString(newWeatherDTO))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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

    public NewWeatherDTO generateNewWeatherDTO(double temperature, LocalDateTime localDateTime, int handbookId) {
        return new NewWeatherDTO(temperature, localDateTime, handbookId);
    }
}
