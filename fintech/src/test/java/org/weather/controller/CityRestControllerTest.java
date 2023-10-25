package org.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.dto.CityDTO;
import org.weather.exception.city.CityAlreadyExistsException;
import org.weather.exception.city.CityNotFoundException;
import org.weather.service.CityService;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CityRestController.class)
public class CityRestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CityService cityService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getCities_ShouldReturnCityList_WhenDataExists() throws Exception {
        CityDTO cityDTO = generateCityDTO("Minsk");
        when(cityService.findAll()).thenReturn(List.of(cityDTO));
        mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(cityDTO))));
    }

    @Test
    public void getCities_ShouldReturnEmptyCityList_WhenNoData() throws Exception {
        when(cityService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/cities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getCityByName_ShouldReturnCityDTO_WhenCityExists() throws Exception {
        CityDTO cityDTO = generateCityDTO("Minsk");
        when(cityService.findCityByName("Minsk")).thenReturn(cityDTO);
        mockMvc.perform(get("/cities/name/Minsk"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cityDTO)));
    }

    @Test
    public void getCityByName_ShouldThrowNotFound_WhenCityDoesntExists() throws Exception {
        when(cityService.findCityByName("Test"))
                .thenThrow(new CityNotFoundException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        mockMvc.perform(get("/cities/name/Test"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCityById_ShouldReturnCityDTO_WhenCityExists() throws Exception {
        CityDTO cityDTO = generateCityDTO("Minsk");
        when(cityService.findCityById(cityDTO.getId())).thenReturn(cityDTO);
        mockMvc.perform(get("/cities/" + cityDTO.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cityDTO)));
    }

    @Test
    public void getCityById_ShouldThrowNotFound_WhenCityDoesntExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(cityService.findCityById(id))
                .thenThrow(new CityNotFoundException(HttpStatus.NOT_FOUND, "NOT FOUND"));
        mockMvc.perform(get("/cities/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveCity_ShouldReturnCreatedStatusAndCityDTO_WhenCityDoesntExist() throws Exception {
        CityDTO cityDTO = generateCityDTO("Minsk");
        when(cityService.save("Minsk")).thenReturn(cityDTO);
        mockMvc.perform(post("/cities/Minsk"))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(cityDTO)))
                .andReturn();
    }

    @Test
    public void saveCity_ShouldThrowBadRequest_WhenCityAlreadyExists() throws Exception {
        when(cityService.save("Test"))
                .thenThrow(new CityAlreadyExistsException(HttpStatus.BAD_REQUEST, "BAD REQUEST"));
        mockMvc.perform(post("/cities/Test"))
                .andExpect(status().isBadRequest());
    }

    private CityDTO generateCityDTO(String cityName) {
        return new CityDTO(UUID.randomUUID(), cityName);
    }
}
