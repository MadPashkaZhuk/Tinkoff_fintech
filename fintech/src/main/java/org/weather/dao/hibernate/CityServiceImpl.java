package org.weather.dao.hibernate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weather.dto.CityDTO;
import org.weather.entity.CityEntity;
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
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "true")
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final WeatherServiceImpl weatherServiceImpl;
    private final MessageSourceWrapper messageSourceWrapper;
    public CityServiceImpl(CityRepository cityRepository, WeatherServiceImpl weatherServiceImpl, MessageSourceWrapper messageSourceWrapper) {
        this.cityRepository = cityRepository;
        this.weatherServiceImpl = weatherServiceImpl;
        this.messageSourceWrapper = messageSourceWrapper;
    }

    public CityEntity save(String cityName) {
        if(cityRepository.getCityByName(cityName) != null) {
            throw new CityAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_ALREADY_EXISTS));
        }
        CityEntity newCity = new CityEntity();
        newCity.setName(cityName);
        cityRepository.save(newCity);
        return newCity;
    }

    public List<CityEntity> findAll() {
        return cityRepository.findAll();
    }
    public CityEntity findCityById(UUID id) {
        Optional<CityEntity> city = cityRepository.findById(id);
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

    public CityEntity getCityByNameOrThrowException(String cityName) {
        CityEntity city = this.findCityByName(cityName);
        if(city == null) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return city;
    }
    @Transactional
    public CityEntity update(String cityName, CityDTO cityDTO) {
        CityEntity existingCity = this.getCityByNameOrThrowException(cityName);
        cityRepository.updateCityNameById(existingCity.getId(), cityDTO.getNewName());
        existingCity = cityRepository.getCityByName(cityName);
        return existingCity;
    }

    @Override
    public CityEntity findCityByName(String cityName) {
        return cityRepository.getCityByName(cityName);
    }
}
