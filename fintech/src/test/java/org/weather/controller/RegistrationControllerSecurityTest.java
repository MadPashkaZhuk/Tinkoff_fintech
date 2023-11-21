package org.weather.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.dao.hibernate.RegistrationServiceImpl;
import org.weather.dto.UserCredentialsDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RegistrationServiceImpl registrationService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithAnonymousUser
    public void saveUser_ShouldReturnUnauthorizedStatus_WhenAnonymous() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO("test",
                new BCryptPasswordEncoder().encode("test").toCharArray());
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCredentialsDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"USER", "ADMIN"})
    public void saveUser_ShouldReturnCreatedStatus_WhenAuthorized() throws Exception {
        UserCredentialsDTO userCredentialsDTO = new UserCredentialsDTO("test",
                new BCryptPasswordEncoder().encode("test").toCharArray());
        mockMvc.perform(post("/api/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCredentialsDTO)))
                .andExpect(status().isCreated());
    }
}
