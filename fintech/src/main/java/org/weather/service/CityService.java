package org.weather.service;

import org.weather.dto.CityDTO;
import org.weather.entity.CityEntity;

import java.util.List;
import java.util.UUID;

public interface CityService {
    CityEntity save(String cityName);
    void delete(String cityName);
    List<CityEntity> findAll();
    CityEntity findCityById(UUID id);
    CityEntity update(String cityName, CityDTO cityDTO);
    CityEntity findCityByName(String cityName);
}
