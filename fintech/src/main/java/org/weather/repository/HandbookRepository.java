package org.weather.repository;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.weather.entity.Handbook;

@ConditionalOnProperty(value = "hibernate.enable", havingValue = "true")
public interface HandbookRepository extends JpaRepository<Handbook, Integer> {}
