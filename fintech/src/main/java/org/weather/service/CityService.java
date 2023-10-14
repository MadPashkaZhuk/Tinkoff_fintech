package org.weather.service;

import org.weather.dto.CityDTO;
import org.weather.entity.City;

import java.util.List;
import java.util.UUID;

public interface CityService {
    City save(String cityName);
    void delete(String cityName);
    List<City> findAll();
    City findCityById(UUID id);
    City update(String cityName, CityDTO cityDTO);
    City findCityByName(String cityName);
}
