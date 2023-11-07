package org.weather.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weather.dto.UserCredentialsDTO;
import org.weather.dao.hibernate.RegistrationServiceImpl;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/signup")
public class RegistrationController {
    private final RegistrationServiceImpl registrationService;

    @PostMapping
    public ResponseEntity<?> saveUser(@RequestBody UserCredentialsDTO userCredentialsDTO) {
        registrationService.addDefaultUser(userCredentialsDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
