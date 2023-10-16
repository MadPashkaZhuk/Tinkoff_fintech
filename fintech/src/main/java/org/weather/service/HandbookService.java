package org.weather.service;

import org.weather.dto.HandbookDTO;

import java.util.List;

public interface HandbookService {
    List<HandbookDTO> findAll();
    HandbookDTO findById(Integer id);
}
