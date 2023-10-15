package org.weather.dao.jdbc;

import org.springframework.jdbc.core.JdbcTemplate;
import org.weather.entity.HandbookEntity;
import org.weather.service.HandbookService;

import javax.sql.DataSource;
import java.util.List;

public class HandbookServiceJdbcImpl implements HandbookService {
    private final JdbcTemplate jdbcTemplate;

    public HandbookServiceJdbcImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<HandbookEntity> findAll() {
        String findAllQuery = "SELECT * FROM handbook";
        return jdbcTemplate.query(findAllQuery, (rs, rowNum) -> {
            HandbookEntity handbook = new HandbookEntity();
            handbook.setId(rs.getInt("id"));
            handbook.setWeatherType(rs.getString("type"));
            return handbook;
        });
    }

    @Override
    public HandbookEntity findById(Integer id) {
        String findByIdQuery = "SELECT * FROM handbook WHERE id = ?";
        return jdbcTemplate.queryForObject(findByIdQuery, (rs, rowNum) -> {
            HandbookEntity handbook = new HandbookEntity();
            handbook.setId(rs.getInt("id"));
            handbook.setWeatherType(rs.getString("type"));
            return handbook;
        }, id);
    }
}
