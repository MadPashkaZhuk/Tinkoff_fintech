package org.weather.dao.hibernate;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.weather.cache.WeatherCache;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.NewWeatherDTO;
import org.weather.dto.WeatherDTO;
import org.weather.entity.CityEntity;
import org.weather.entity.HandbookEntity;
import org.weather.entity.WeatherEntity;
import org.weather.exception.weather.WeatherNotFoundException;
import org.weather.repository.WeatherRepository;
import org.weather.service.WeatherService;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;
    private final CityServiceImpl cityServiceImpl;
    private final HandbookServiceImpl handbookServiceImpl;
    private final MessageSourceWrapper messageSourceWrapper;
    private final EntityMapper entityMapper;
    private final WeatherCache weatherCache;

    public WeatherServiceImpl(WeatherRepository weatherRepository,
                              @Lazy CityServiceImpl cityServiceImpl,
                              HandbookServiceImpl handbookServiceImpl,
                              MessageSourceWrapper messageSourceWrapper, EntityMapper entityMapper,
                              WeatherCache weatherCache) {
        this.weatherRepository = weatherRepository;
        this.cityServiceImpl = cityServiceImpl;
        this.handbookServiceImpl = handbookServiceImpl;
        this.messageSourceWrapper = messageSourceWrapper;
        this.entityMapper = entityMapper;
        this.weatherCache = weatherCache;
    }

    public WeatherDTO getWeatherForCity(String cityName) {
        Optional<WeatherDTO> cacheVal = weatherCache.getWeather(cityName);
        if(cacheVal.isPresent()) {
            return cacheVal.get();
        }
        WeatherDTO currentData = getWeatherHistoryForCity(cityName).stream()
                .max(Comparator.comparing(WeatherDTO::getDateTime)).get();
        weatherCache.addWeather(currentData);
        return currentData;
    }

    public List<WeatherDTO> getWeatherHistoryForCity(String cityName) {
        CityEntity city = getCityEntityByNameFromRepo(cityName);
        return mapWeatherEntityListToDtoList(weatherRepository.findWeatherByCity(city));
    }

    public WeatherDTO saveWeatherForCity(String cityName, NewWeatherDTO newWeatherData) {
        CityEntity city = getCityEntityByNameFromRepo(cityName);
        if(weatherRepository.getWeatherByCityAndDatetime(city, newWeatherData.getDateTime()) != null) {
            throw new WeatherNotFoundException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.WEATHER_ALREADY_EXISTS));
        }
        WeatherEntity weather = new WeatherEntity(newWeatherData.getTemp_val(),
                city,
                newWeatherData.getDateTime(),
                getHandbookEntityById(newWeatherData.getHandbook_id()));
        weatherRepository.save(weather);

        WeatherDTO weatherDTO = mapWeatherEntityToDTO(weather);
        weatherCache.updateWeather(weatherDTO);
        return weatherDTO;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteWeatherByDateTime(String cityName, NewWeatherDTO weatherToDelete) {
        CityEntity city = getCityEntityByNameFromRepo(cityName);
        weatherRepository.deleteWeatherByCityAndDatetime(city,
                weatherToDelete.getDateTime());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteAll(String cityName){
        CityEntity city = getCityEntityByNameFromRepo(cityName);
        weatherRepository.deleteAllByCity(city);
    }

    public List<WeatherDTO> findAll() {
        return mapWeatherEntityListToDtoList(weatherRepository.findAll());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public WeatherDTO updateWeatherForCity(String cityName, NewWeatherDTO newWeatherData) {
        if(!cityServiceImpl.hasCityWithName(cityName)) {
            cityServiceImpl.save(cityName);
        }
        CityEntity city = getCityEntityByNameFromRepo(cityName);
        WeatherEntity currentWeather = weatherRepository.getWeatherByCityAndDatetime(city, newWeatherData.getDateTime());
        if (currentWeather == null) {
            return saveWeatherForCity(cityName, newWeatherData);
        }
        return updateExistingWeather(currentWeather, newWeatherData);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public WeatherDTO updateExistingWeather(WeatherEntity currentWeather, NewWeatherDTO newWeatherData) {
        HandbookEntity handbook = getHandbookEntityById(newWeatherData.getHandbook_id());
        weatherRepository.updateWeatherById(currentWeather.getId(),
                newWeatherData.getTemp_val(), handbook);
        WeatherDTO weatherDTO =  new WeatherDTO(currentWeather.getId(), newWeatherData.getTemp_val(),
                currentWeather.getDatetime(), mapCityEntityToDTO(currentWeather.getCity()),
                mapHandbookEntityToDTO(getHandbookEntityById(newWeatherData.getHandbook_id())));
        weatherCache.updateWeather(weatherDTO);
        return weatherDTO;
    }

    private CityEntity getCityEntityByNameFromRepo(String cityName) {
        return cityServiceImpl.getCityEntityByNameOrThrowException(cityName);
    }

    private HandbookEntity getHandbookEntityById(Integer handbook_id) {
        return handbookServiceImpl.getHandbookEntityById(handbook_id);
    }

    private WeatherDTO mapWeatherEntityToDTO(WeatherEntity weatherEntity) {
        return entityMapper.mapWeatherEntityToDTO(weatherEntity);
    }

    private List<WeatherDTO> mapWeatherEntityListToDtoList(List<WeatherEntity> weatherEntityList) {
        return entityMapper.mapWeatherEntityListToDtoList(weatherEntityList);
    }

    private HandbookDTO mapHandbookEntityToDTO(HandbookEntity handbookEntity) {
        return entityMapper.mapHandbookEntityToDTO(handbookEntity);
    }

    private CityDTO mapCityEntityToDTO(CityEntity cityEntity) {
        return entityMapper.mapCityEntityToDTO(cityEntity);
    }
}
