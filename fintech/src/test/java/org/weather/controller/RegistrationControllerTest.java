package org.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.dto.UserCredentialsDTO;
import org.weather.dao.hibernate.RegistrationServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    RegistrationServiceImpl registrationService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    void saveUser_ShouldReturnCreatedStatus_WhenSystemWorks() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO("test", "test");
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCredentialsDTO)))
                .andExpect(status().isCreated());
    }
}