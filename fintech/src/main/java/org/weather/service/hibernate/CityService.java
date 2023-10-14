package org.weather.service.hibernate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weather.dto.CityDTO;
import org.weather.entity.City;
import org.weather.exception.city.CityAlreadyExistsException;
import org.weather.exception.city.CityNotFoundException;
import org.weather.repository.hibernate.CityRepository;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CityService {
    private final CityRepository cityRepository;
    private final WeatherService weatherService;
    private final MessageSourceWrapper messageSourceWrapper;

    public City save(String cityName) {
        if(cityRepository.getCityByName(cityName) != null) {
            throw new CityAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_ALREADY_EXISTS));
        }
        City newCity = new City();
        newCity.setName(cityName);
        cityRepository.save(newCity);
        return newCity;
    }

    public List<City> findAll() {
        return cityRepository.findAll();
    }
    public City findCityById(UUID id) {
        Optional<City> city = cityRepository.findById(id);
        if(city.isEmpty()) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return city.get();
    }

    @Transactional
    public void deleteCity(String cityName) {
        if(cityRepository.getCityByName(cityName) != null) {
            weatherService.deleteAll(cityName);
            cityRepository.deleteCityByName(cityName);
        }
    }

    public City getCityByNameOrThrowException(String cityName) {
        City city = cityRepository.getCityByName(cityName);
        if(city == null) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return city;
    }
    @Transactional
    public City updateCityByName(String cityName, CityDTO cityDTO) {
        City existingCity = this.getCityByNameOrThrowException(cityName);
        cityRepository.updateCityNameById(existingCity.getId(), cityDTO.getNewName());
        existingCity = cityRepository.getCityByName(cityName);
        return existingCity;
    }
}
