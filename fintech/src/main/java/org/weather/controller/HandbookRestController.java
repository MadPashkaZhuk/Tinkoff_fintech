package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.weather.entity.Handbook;
import org.weather.service.hibernate.HandbookService;

import java.util.List;

@RestController
@RequestMapping("/handbook")
@RequiredArgsConstructor
public class HandbookRestController {
    private final HandbookService handbookService;

    @GetMapping
    @Operation(summary = "Get all handbook types",
            description = "Get all handbook types. There are 9 types for weather.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All information is shown")
    })
    ResponseEntity<List<Handbook>> getAllHandbookTypes() {
        return ResponseEntity.ok()
                .body(handbookService.findAll());
    }
    @Operation(summary = "Get handbook type by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Handbook type retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Handbook with this id doesn't exist in database")
    })
    @GetMapping("/{id}")
    ResponseEntity<Handbook> getHandbookTypeById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok()
                .body(handbookService.findById(id));
    }
}