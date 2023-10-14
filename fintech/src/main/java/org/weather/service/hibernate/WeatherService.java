package org.weather.service.hibernate;

import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weather.dto.WeatherDTO;
import org.weather.entity.City;
import org.weather.entity.Handbook;
import org.weather.entity.Weather;
import org.weather.exception.weather.WeatherNotFoundException;
import org.weather.repository.hibernate.WeatherRepository;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;

@Service
public class WeatherService {
    private final WeatherRepository weatherRepository;
    private final CityService cityService;
    private final HandbookService handbookService;
    private final MessageSourceWrapper messageSourceWrapper;

    public WeatherService(WeatherRepository weatherRepository,
                          @Lazy CityService cityService,
                          HandbookService handbookService,
                          MessageSourceWrapper messageSourceWrapper) {
        this.weatherRepository = weatherRepository;
        this.cityService = cityService;
        this.handbookService = handbookService;
        this.messageSourceWrapper = messageSourceWrapper;
    }

    public List<Weather> getWeatherForCity(String cityName) {
        City city = getCityByName(cityName);
        return weatherRepository.findWeatherByCity(city);
    }

    public Weather saveWeatherForCity(String cityName, WeatherDTO newWeatherData) {
        City city = getCityByName(cityName);
        if(weatherRepository.getWeatherByCityAndDatetime(city, newWeatherData.getDateTime()) != null) {
            throw new WeatherNotFoundException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.WEATHER_ALREADY_EXISTS));
        }
        Weather weather = new Weather(newWeatherData.getTemp_val(),
                city,
                newWeatherData.getDateTime(),
                getHandbookById(newWeatherData.getHandbook_id()));
        weatherRepository.save(weather);
        return weather;
    }

    @Transactional
    public void deleteWeatherByDateTime(String cityName, WeatherDTO weatherToDelete) {
        City city = getCityByName(cityName);
        weatherRepository.deleteWeatherByCityAndDatetime(city,
                weatherToDelete.getDateTime());
    }

    @Transactional
    public void deleteAll(String cityName){
        City city = getCityByName(cityName);
        weatherRepository.deleteAllByCity(city);
    }

    public List<Weather> findAll() {
        return weatherRepository.findAll();
    }

    @Transactional
    public Weather updateWeatherForCity(String cityName, WeatherDTO newWeatherData) {
        City city = getCityByName(cityName);
        Weather currentWeather = weatherRepository.getWeatherByCityAndDatetime(city, newWeatherData.getDateTime());
        if (currentWeather == null) {
            return saveWeatherForCity(cityName, newWeatherData);
        }
        return updateExistingWeather(currentWeather, newWeatherData);
    }

    @Transactional
    public Weather updateExistingWeather(Weather currentWeather, WeatherDTO newWeatherData) {
        Handbook handbook = getHandbookById(newWeatherData.getHandbook_id());
        weatherRepository.updateWeatherById(currentWeather.getId(),
                newWeatherData.getTemp_val(), handbook);
        currentWeather.setHandbook(handbook);
        currentWeather.setTemp_c(newWeatherData.getTemp_val());
        return currentWeather;
    }

    private City getCityByName(String cityName) {
        return cityService.getCityByNameOrThrowException(cityName);
    }

    private Handbook getHandbookById(Integer handbook_id) {
        return handbookService.findById(handbook_id);
    }
}
