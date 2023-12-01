package org.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.weather.entity.CityEntity;
import org.weather.entity.HandbookEntity;
import org.weather.entity.WeatherEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WeatherRepository extends JpaRepository<WeatherEntity, UUID> {
    List<WeatherEntity> findWeatherByCity(CityEntity city);
    @Modifying
    @Query("DELETE FROM WeatherEntity w WHERE w.city = :city AND w.datetime = :dateTime")
    @Transactional
    void deleteWeatherByCityAndDatetime(@Param("city") CityEntity city, @Param("dateTime") LocalDateTime dateTime);

    @Modifying
    @Query("UPDATE WeatherEntity w SET w.temp_c = :temperature, w.handbook = :handbook WHERE w.id = :weatherId")
    @Transactional
    void updateWeatherById(@Param("weatherId") UUID weatherId, @Param("temperature") double temperature,
                           @Param("handbook") HandbookEntity handbook);
    @Transactional
    void deleteAllByCity(CityEntity city);
    WeatherEntity getWeatherByCityAndDatetime(CityEntity city, LocalDateTime dateTime);
    List<WeatherEntity> findTop30ByCityOrderByDatetimeDesc(CityEntity city);
}
