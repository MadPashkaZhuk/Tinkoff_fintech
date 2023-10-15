package org.weather.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.weather.entity.City;

import java.util.UUID;

@Repository
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "true")
public interface CityRepository extends JpaRepository<City, UUID> {
    City getCityByName(String cityName);
    @Transactional
    void deleteCityByName(String cityName);
    @Modifying
    @Query("UPDATE City c SET c.name = :newCityName where c.id = :cityId")
    @Transactional
    void updateCityNameById(@Param("cityId") UUID cityId, @Param("newCityName") String newCityName);
}
