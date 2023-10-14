package org.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.weather.entity.Handbook;

public interface HandbookRepository extends JpaRepository<Handbook, Integer> {}
