package org.weather.utils;

import org.springframework.stereotype.Component;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.WeatherDTO;
import org.weather.entity.CityEntity;
import org.weather.entity.HandbookEntity;
import org.weather.entity.WeatherEntity;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityMapper {
    public CityDTO mapCityEntityToDTO(CityEntity city) {
        return new CityDTO(city.getId(), city.getName());
    }

    public List<CityDTO> mapCityEntityListToDtoList(List<CityEntity> cityEntityList) {
        return cityEntityList.stream()
                .map(this::mapCityEntityToDTO)
                .collect(Collectors.toList());
    }

    public WeatherDTO mapWeatherEntityToDTO(WeatherEntity weatherEntity) {
        return new WeatherDTO(weatherEntity.getId(), weatherEntity.getTemp_c(), weatherEntity.getDatetime(),
                mapCityEntityToDTO(weatherEntity.getCity()),
                mapHandbookEntityToDTO(weatherEntity.getHandbook()));
    }

    public List<WeatherDTO> mapWeatherEntityListToDtoList(List<WeatherEntity> weatherEntityList) {
        return weatherEntityList.stream()
                .map(this::mapWeatherEntityToDTO)
                .collect(Collectors.toList());
    }

    public HandbookDTO mapHandbookEntityToDTO(HandbookEntity handbookEntity) {
        return new HandbookDTO(handbookEntity.getId(), handbookEntity.getWeatherType());
    }

    public List<HandbookDTO> mapHandbookEntityListToDtoList(List<HandbookEntity> handbookEntityList) {
        return handbookEntityList.stream()
                .map(this::mapHandbookEntityToDTO)
                .collect(Collectors.toList());
    }
}
