package org.weather.repository.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.weather.entity.Handbook;

public interface HandbookRepository extends JpaRepository<Handbook, Integer> {}
