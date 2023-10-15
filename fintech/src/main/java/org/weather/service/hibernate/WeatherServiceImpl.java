package org.weather.service.hibernate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.weather.dto.WeatherDTO;
import org.weather.entity.City;
import org.weather.entity.Handbook;
import org.weather.entity.Weather;
import org.weather.exception.weather.WeatherNotFoundException;
import org.weather.repository.WeatherRepository;
import org.weather.service.WeatherService;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;

@Service
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "true")
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
        return cityServiceImpl.getCityByNameOrThrowException(cityName);
    }

    private Handbook getHandbookById(Integer handbook_id) {
        return handbookServiceImpl.findById(handbook_id);
    }
}
