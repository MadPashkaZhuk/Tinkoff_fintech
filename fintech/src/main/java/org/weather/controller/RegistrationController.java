package org.weather.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weather.dto.UserCredentialsDTO;
import org.weather.service.RegistrationService;

@RestController
@RequestMapping("/api/signup")
public class RegistrationController {
    RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping
    public void saveUser(@RequestBody UserCredentialsDTO userCredentialsDTO) {
        registrationService.addDefaultUser(userCredentialsDTO);
    }
}
