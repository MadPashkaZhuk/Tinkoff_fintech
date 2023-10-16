package org.weather.service;

import org.weather.dto.CityDTO;
import org.weather.dto.NewCityDTO;

import java.util.List;
import java.util.UUID;

public interface CityService {
    CityDTO save(String cityName);
    void delete(String cityName);
    List<CityDTO> findAll();
    CityDTO findCityById(UUID id);
    CityDTO update(String cityName, NewCityDTO newCityDTO);
    CityDTO findCityByName(String cityName);
}
