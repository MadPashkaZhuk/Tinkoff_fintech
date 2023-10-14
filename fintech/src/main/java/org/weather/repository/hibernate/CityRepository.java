package org.weather.repository.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.weather.entity.City;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    City getCityByName(String cityName);
    void deleteCityByName(String cityName);
    @Modifying
    @Query("UPDATE City c SET c.name = :newCityName where c.id = :cityId")
    void updateCityNameById(@Param("cityId") UUID cityId, @Param("newCityName") String newCityName);
}
