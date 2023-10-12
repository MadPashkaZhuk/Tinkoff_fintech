package org.weather.repository.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.weather.entity.City;
import org.weather.entity.Weather;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, UUID> {
    List<Weather> findWeatherByCity(City city);
    void deleteWeatherByCityAndDatetime(City city, LocalDateTime localDateTime);
    void deleteAllByCity(City city);
}
