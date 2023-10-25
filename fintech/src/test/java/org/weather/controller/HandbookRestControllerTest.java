package org.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.dto.HandbookDTO;
import org.weather.exception.handbook.HandbookTypeNotFoundException;
import org.weather.service.HandbookService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HandbookRestController.class)
public class HandbookRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    HandbookService handbookService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void getAllHandbookTypes_ShouldReturnHandbookList_WhenDataExists() throws Exception {
        HandbookDTO handbookDTO1 = generateHandbookDTO(1, "Sunshine");
        HandbookDTO handbookDTO2 = generateHandbookDTO(2, "Snowing");
        when(handbookService.findAll()).thenReturn(List.of(
                handbookDTO1,
                handbookDTO2
        ));
        mockMvc.perform(get("/handbook"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(
                        handbookDTO1,
                        handbookDTO2
                ))));

    }

    @Test
    public void getAllHandbookTypes_ShouldReturnEmptyHandbookList_WhenNoData() throws Exception {
        when(handbookService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/handbook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void getHandbookTypeById_ShouldReturnHandbookDTO_WhenHandbookExists() throws Exception {
        HandbookDTO handbookDTO = generateHandbookDTO(1, "Sunshine");
        when(handbookService.findById(1)).thenReturn(handbookDTO);
        mockMvc.perform(get("/handbook/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(handbookDTO)));
    }

    @Test
    public void getHandbookTypeById_ShouldThrowNotFound_WhenHandbookDoesntExist() throws Exception {
       when(handbookService.findById(1)).
               thenThrow(new HandbookTypeNotFoundException(HttpStatus.NOT_FOUND, "NOT FOUND"));
       mockMvc.perform(get("/handbook/1"))
               .andExpect(status().isNotFound());
    }

    public HandbookDTO generateHandbookDTO(int id, String typeName) {
        return new HandbookDTO(id, typeName);
    }
}
