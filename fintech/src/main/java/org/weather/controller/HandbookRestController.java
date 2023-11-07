package org.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.weather.dto.HandbookDTO;
import org.weather.service.HandbookService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/handbook")
public class HandbookRestController {
    private final HandbookService handbookService;

    @GetMapping
    @Operation(summary = "Get all handbook types")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All information is shown")
    })
    ResponseEntity<List<HandbookDTO>> getAllHandbookTypes() {
        return ResponseEntity.ok()
                .body(handbookService.findAll());
    }
    @Operation(summary = "Get handbook type by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Handbook type retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Handbook with this id doesn't exist in database")
    })
    @GetMapping("/{id}")
    ResponseEntity<HandbookDTO> getHandbookTypeById(@PathVariable("id") Integer id) {
        return ResponseEntity.ok()
                .body(handbookService.findById(id));
    }

    @Operation(summary = "Add new handbook type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New handbook type created successfully"),
            @ApiResponse(responseCode = "400", description = "Handbook with this type already created")
    })
    @PostMapping("/{newType}")
    public ResponseEntity<HandbookDTO> saveHandbook(@PathVariable("newType") String newType,
                                                    UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/handbook/{newType}")
                        .build(Map.of("newType", newType)))
                .contentType(MediaType.APPLICATION_JSON)
                .body(handbookService.save(newType));
    }
}
