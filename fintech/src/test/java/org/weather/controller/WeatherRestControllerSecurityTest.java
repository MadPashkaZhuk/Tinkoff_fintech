package org.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.dto.NewWeatherDTO;
import org.weather.service.CityService;
import org.weather.service.WeatherService;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherRestControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private WeatherService weatherService;
    @MockBean
    private CityService cityService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void getAllWeather_ShouldReturnOkStatus_WhenAuthorized() throws Exception {
        mockMvc.perform(get("/api/weather"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void getAlLWeather_ShouldReturnUnauthorizedStatus_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/weather"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void getWeatherForCity_ShouldReturnOkStatus_WhenAuthorized() throws Exception {
        mockMvc.perform(get("/api/weather/Minsk"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void getWeatherForCity_ShouldReturnUnauthorizedStatus_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/weather/Minsk"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void saveWeather_ShouldReturnCreatedStatus_WhenAdminAuthorized() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 5, 18, 45);
        NewWeatherDTO newWeatherDTO = generateNewWeatherDTO(15, localDateTime, 1);
        mockMvc.perform(post("/api/weather/Minsk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWeatherDTO)))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void saveWeather_ShouldReturnForbiddenStatus_WhenUserAuthorized() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 5, 18, 45);
        NewWeatherDTO newWeatherDTO = generateNewWeatherDTO(15, localDateTime, 1);
        mockMvc.perform(post("/api/weather/Minsk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWeatherDTO)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void saveWeather_ShouldReturnUnauthorizedStatus_WhenAnonymous() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2023, 11, 5, 18, 45);
        NewWeatherDTO newWeatherDTO = generateNewWeatherDTO(15, localDateTime, 1);
        mockMvc.perform(post("/api/weather/Minsk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newWeatherDTO)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    public NewWeatherDTO generateNewWeatherDTO(double temperature, LocalDateTime localDateTime, int handbookId) {
        return new NewWeatherDTO(temperature, localDateTime, handbookId);
    }
}
