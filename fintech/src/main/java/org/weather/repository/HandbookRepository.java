package org.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.weather.entity.HandbookEntity;

@Repository
public interface HandbookRepository extends JpaRepository<HandbookEntity, Integer> {
    HandbookEntity getHandbookEntityByWeatherType(String weatherType);
}
