package org.weather.service;

import org.weather.dto.HandbookDTO;

import java.util.List;

public interface HandbookService {
    HandbookDTO save(String typeName);
    List<HandbookDTO> findAll();
    HandbookDTO findById(Integer id);
    HandbookDTO getOrCreateByTypeName(String typeName);
}
