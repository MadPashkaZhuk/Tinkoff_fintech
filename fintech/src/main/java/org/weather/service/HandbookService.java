package org.weather.service;

import org.weather.entity.Handbook;

import java.util.List;

public interface HandbookService {
    List<Handbook> findAll();
    Handbook findById(Integer id);
}
