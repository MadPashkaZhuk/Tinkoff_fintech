package org.weather.dao.hibernate;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.weather.dto.WeatherDTO;
import org.weather.entity.CityEntity;
import org.weather.entity.HandbookEntity;
import org.weather.entity.WeatherEntity;
import org.weather.exception.weather.WeatherNotFoundException;
import org.weather.repository.WeatherRepository;
import org.weather.service.WeatherService;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;

public class WeatherServiceImpl implements WeatherService {
    private final WeatherRepository weatherRepository;
    private final CityServiceImpl cityServiceImpl;
    private final HandbookServiceImpl handbookServiceImpl;
    private final MessageSourceWrapper messageSourceWrapper;

    public WeatherServiceImpl(WeatherRepository weatherRepository,
                              @Lazy CityServiceImpl cityServiceImpl,
                              HandbookServiceImpl handbookServiceImpl,
                              MessageSourceWrapper messageSourceWrapper) {
        this.weatherRepository = weatherRepository;
        this.cityServiceImpl = cityServiceImpl;
        this.handbookServiceImpl = handbookServiceImpl;
        this.messageSourceWrapper = messageSourceWrapper;
    }

    public List<WeatherEntity> getWeatherForCity(String cityName) {
        CityEntity city = getCityByNameFromRepo(cityName);
        return weatherRepository.findWeatherByCity(city);
    }

    public WeatherEntity saveWeatherForCity(String cityName, WeatherDTO newWeatherData) {
        CityEntity city = getCityByNameFromRepo(cityName);
        if(weatherRepository.getWeatherByCityAndDatetime(city, newWeatherData.getDateTime()) != null) {
            throw new WeatherNotFoundException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.WEATHER_ALREADY_EXISTS));
        }
        WeatherEntity weather = new WeatherEntity(newWeatherData.getTemp_val(),
                city,
                newWeatherData.getDateTime(),
                getHandbookById(newWeatherData.getHandbook_id()));
        weatherRepository.save(weather);
        return weather;
    }

    @Transactional
    public void deleteWeatherByDateTime(String cityName, WeatherDTO weatherToDelete) {
        CityEntity city = getCityByNameFromRepo(cityName);
        weatherRepository.deleteWeatherByCityAndDatetime(city,
                weatherToDelete.getDateTime());
    }

    @Transactional
    public void deleteAll(String cityName){
        CityEntity city = getCityByNameFromRepo(cityName);
        weatherRepository.deleteAllByCity(city);
    }

    public List<WeatherEntity> findAll() {
        return weatherRepository.findAll();
    }

    @Transactional
    public WeatherEntity updateWeatherForCity(String cityName, WeatherDTO newWeatherData) {
        CityEntity city = getCityByNameFromRepo(cityName);
        WeatherEntity currentWeather = weatherRepository.getWeatherByCityAndDatetime(city, newWeatherData.getDateTime());
        if (currentWeather == null) {
            return saveWeatherForCity(cityName, newWeatherData);
        }
        return updateExistingWeather(currentWeather, newWeatherData);
    }

    @Transactional
    public WeatherEntity updateExistingWeather(WeatherEntity currentWeather, WeatherDTO newWeatherData) {
        HandbookEntity handbook = getHandbookById(newWeatherData.getHandbook_id());
        weatherRepository.updateWeatherById(currentWeather.getId(),
                newWeatherData.getTemp_val(), handbook);
        currentWeather.setHandbook(handbook);
        currentWeather.setTemp_c(newWeatherData.getTemp_val());
        return currentWeather;
    }

    private CityEntity getCityByNameFromRepo(String cityName) {
        return cityServiceImpl.getCityByNameOrThrowException(cityName);
    }

    private HandbookEntity getHandbookById(Integer handbook_id) {
        return handbookServiceImpl.findById(handbook_id);
    }
}
