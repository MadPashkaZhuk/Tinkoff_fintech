package org.weather.repository.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.weather.entity.City;
import org.weather.entity.Handbook;
import org.weather.entity.Weather;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, UUID> {
    List<Weather> findWeatherByCity(City city);
    @Modifying
    @Query("DELETE FROM Weather w WHERE w.city = :city AND w.datetime = :dateTime")
    void deleteWeatherByCityAndDatetime(@Param("city") City city, @Param("dateTime") LocalDateTime dateTime);

    @Modifying
    @Query("UPDATE Weather w SET w.temp_c = :temperature, w.handbook = :handbook WHERE w.id = :weatherId")
    void updateWeatherById(@Param("weatherId") UUID weatherId, @Param("temperature") double temperature,
                           @Param("handbook") Handbook handbook);
    void deleteAllByCity(City city);
    Weather getWeatherByCityAndDatetime(City city, LocalDateTime dateTime);
}
