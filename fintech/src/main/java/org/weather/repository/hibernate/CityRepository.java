package org.weather.repository.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.weather.entity.City;

import java.util.UUID;

@Repository
public interface CityRepository extends JpaRepository<City, UUID> {
    City getCityByName(String cityName);
    void deleteCityByName(String cityName);
}
