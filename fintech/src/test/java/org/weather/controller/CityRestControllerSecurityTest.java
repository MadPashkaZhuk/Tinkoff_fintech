package org.weather.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.weather.service.CityService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CityRestControllerSecurityTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CityService cityService;

    @Test
    @WithMockUser(authorities = {"USER", "ADMIN"})
    public void getCities_ShouldReturnOkStatus_WhenAuthorized() throws Exception {
        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void getCities_ShouldReturnUnauthorizedStatus_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/cities"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser(authorities = {"ADMIN"})
    public void saveCity_ShouldReturnCreatedStatus_WhenAdminAuthorized() throws Exception {
        mockMvc.perform(post("/api/cities/Minsk"))
                .andExpect(status().isCreated())
                .andReturn();
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    public void saveCity_ShouldReturnForbiddenStatus_WhenUserAuthorized() throws Exception {
        mockMvc.perform(post("/api/cities/Minsk"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void saveCity_ShouldReturnUnauthorizedStatus_WhenAnonymous() throws Exception {
        mockMvc.perform(post("/api/cities/Minsk"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }
}
