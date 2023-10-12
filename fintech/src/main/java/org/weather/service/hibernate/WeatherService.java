package org.weather.service.hibernate;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.weather.dto.WeatherDTO;
import org.weather.entity.City;
import org.weather.entity.Weather;
import org.weather.exception.city.CityNotFoundException;
import org.weather.repository.hibernate.CityRepository;
import org.weather.repository.hibernate.WeatherRepository;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherRepository weatherRepository;
    private final CityRepository cityRepository;
    private final MessageSourceWrapper messageSourceWrapper;

    public List<Weather> getWeatherForCity(String cityName) {
        City city = this.getCityOrThrowException(cityName);
        return weatherRepository.findWeatherByCity(city);
    }

    public Weather saveWeatherForCity(String cityName, WeatherDTO weatherDTO) {
        City city = this.getCityOrThrowException(cityName);
        Weather weather = new Weather(weatherDTO.getTemp_val(), city, weatherDTO.getDateTime());
        weatherRepository.save(weather);
        return weather;
    }

    @Transactional
    public void deleteWeatherByDateTime(String cityName, WeatherDTO weatherDTO) {
        City city = getCityOrThrowException(cityName);
        weatherRepository.deleteWeatherByCityAndDatetime(city, weatherDTO.getDateTime());
    }

    @Transactional
    public void deleteAll(String cityName){
        City city = this.getCityOrThrowException(cityName);
        weatherRepository.deleteAllByCity(city);
    }

    public List<Weather> findAll() {
        return weatherRepository.findAll();
    }

    private City getCityOrThrowException(String cityName) {
        City city = cityRepository.getCityByName(cityName);
        if(city == null) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return city;
    }
}
