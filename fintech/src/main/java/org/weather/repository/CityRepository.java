package org.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.weather.entity.CityEntity;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, UUID> {
    CityEntity getCityByName(String cityName);
    @Transactional
    void deleteCityByName(String cityName);
    @Modifying
    @Query("UPDATE CityEntity c SET c.name = :newCityName where c.id = :cityId")
    @Transactional
    void updateCityNameById(@Param("cityId") UUID cityId, @Param("newCityName") String newCityName);
}
