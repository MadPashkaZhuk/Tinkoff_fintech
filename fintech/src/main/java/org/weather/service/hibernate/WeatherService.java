package org.weather.service.hibernate;

import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.weather.dto.WeatherDTO;
import org.weather.entity.Handbook;
import org.weather.entity.Weather;
import org.weather.repository.hibernate.WeatherRepository;

import java.util.List;

@Service
public class WeatherService {
    private final WeatherRepository weatherRepository;
    private final CityService cityService;

    private final HandbookService handbookService;

    public WeatherService(WeatherRepository weatherRepository,
                          @Lazy CityService cityService,
                          HandbookService handbookService) {
        this.weatherRepository = weatherRepository;
        this.cityService = cityService;
        this.handbookService = handbookService;
    }

    public List<Weather> getWeatherForCity(String cityName) {
        return weatherRepository.findWeatherByCity(cityService.getCityByNameOrThrowException(cityName));
    }

    public Weather saveWeatherForCity(String cityName, WeatherDTO weatherDTO) {
        Handbook handbook = handbookService.findById(weatherDTO.getHandbook_id());
        Weather weather = new Weather(weatherDTO.getTemp_val(),
                cityService.getCityByNameOrThrowException(cityName),
                weatherDTO.getDateTime(),
                handbook);
        weatherRepository.save(weather);
        return weather;
    }

    @Transactional
    public void deleteWeatherByDateTime(String cityName, WeatherDTO weatherDTO) {
        weatherRepository.deleteWeatherByCityAndDatetime(cityService.getCityByNameOrThrowException(cityName),
                weatherDTO.getDateTime());
    }

    @Transactional
    public void deleteAll(String cityName){
        weatherRepository.deleteAllByCity(cityService.getCityByNameOrThrowException(cityName));
    }

    public List<Weather> findAll() {
        return weatherRepository.findAll();
    }

}
