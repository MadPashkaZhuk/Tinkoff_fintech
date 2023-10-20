package org.weather.dao.hibernate;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.weather.dto.CityDTO;
import org.weather.dto.NewCityDTO;
import org.weather.entity.CityEntity;
import org.weather.exception.city.CityAlreadyExistsException;
import org.weather.exception.city.CityNotFoundException;
import org.weather.repository.CityRepository;
import org.weather.service.CityService;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final WeatherServiceImpl weatherServiceImpl;
    private final MessageSourceWrapper messageSourceWrapper;
    private final EntityMapper entityMapper;
    public CityServiceImpl(CityRepository cityRepository, WeatherServiceImpl weatherServiceImpl,
                           MessageSourceWrapper messageSourceWrapper, EntityMapper entityMapper) {
        this.cityRepository = cityRepository;
        this.weatherServiceImpl = weatherServiceImpl;
        this.messageSourceWrapper = messageSourceWrapper;
        this.entityMapper = entityMapper;
    }

    public CityDTO save(String cityName) {
        if(hasCityWithName(cityName)) {
            throw new CityAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_ALREADY_EXISTS));
        }
        CityEntity newCity = new CityEntity();
        newCity.setName(cityName);
        cityRepository.save(newCity);
        return mapCityEntityToDTO(newCity);
    }

    public List<CityDTO> findAll() {
        return mapCityEntityListToDtoList(cityRepository.findAll());
    }

    public CityDTO findCityById(UUID id) {
        Optional<CityEntity> city = cityRepository.findById(id);
        if(city.isEmpty()) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return mapCityEntityToDTO(city.get());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void delete(String cityName) {
        if(hasCityWithName(cityName)) {
            weatherServiceImpl.deleteAll(cityName);
            cityRepository.deleteCityByName(cityName);
        }
    }

    public CityEntity getCityEntityByNameOrThrowException(String cityName) {
        if(!hasCityWithName(cityName)) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return getCityEntityByName(cityName);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public CityDTO update(String cityName, NewCityDTO newCityDTO) {
        CityEntity city = getCityEntityByName(cityName);
        if(hasCityWithName(newCityDTO.getNewName())) {
            delete(cityName);
            return mapCityEntityToDTO(getCityEntityByName(newCityDTO.getNewName()));
        }
        if (city == null) {
            return save(newCityDTO.getNewName());
        }
        cityRepository.updateCityNameById(city.getId(), newCityDTO.getNewName());
        city.setName(newCityDTO.getNewName());
        return mapCityEntityToDTO(city);
    }

    @Override
    public CityDTO findCityByName(String cityName) {
        return mapCityEntityToDTO(getCityEntityByNameOrThrowException(cityName));
    }

    private CityEntity getCityEntityByName(String cityName) {
        return cityRepository.getCityByName(cityName);
    }

    public boolean hasCityWithName(String cityName) {
        return cityRepository.getCityByName(cityName) != null;
    }

    private CityDTO mapCityEntityToDTO(CityEntity city) {
        return entityMapper.mapCityEntityToDTO(city);
    }

    private List<CityDTO> mapCityEntityListToDtoList(List<CityEntity> cityEntityList) {
        return entityMapper.mapCityEntityListToDtoList(cityEntityList);
    }
}
