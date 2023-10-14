package org.weather.service.hibernate;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weather.dto.CityDTO;
import org.weather.entity.City;
import org.weather.exception.city.CityAlreadyExistsException;
import org.weather.exception.city.CityNotFoundException;
import org.weather.repository.CityRepository;
import org.weather.service.CityService;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "true")
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final WeatherServiceImpl weatherServiceImpl;
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
    public void delete(String cityName) {
        if(cityRepository.getCityByName(cityName) != null) {
            weatherServiceImpl.deleteAll(cityName);
            cityRepository.deleteCityByName(cityName);
        }
    }

    public City getCityByNameOrThrowException(String cityName) {
        City city = this.findCityByName(cityName);
        if(city == null) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return city;
    }
    @Transactional
    public City update(String cityName, CityDTO cityDTO) {
        City existingCity = this.getCityByNameOrThrowException(cityName);
        cityRepository.updateCityNameById(existingCity.getId(), cityDTO.getNewName());
        existingCity = cityRepository.getCityByName(cityName);
        return existingCity;
    }

    @Override
    public City findCityByName(String cityName) {
        return cityRepository.getCityByName(cityName);
    }
}
