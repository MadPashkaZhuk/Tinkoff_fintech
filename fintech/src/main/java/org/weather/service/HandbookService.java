package org.weather.service;

import org.weather.entity.HandbookEntity;

import java.util.List;

public interface HandbookService {
    List<HandbookEntity> findAll();
    HandbookEntity findById(Integer id);
}
