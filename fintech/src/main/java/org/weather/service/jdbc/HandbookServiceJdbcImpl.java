package org.weather.service.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.weather.entity.Handbook;
import org.weather.service.HandbookService;

import javax.sql.DataSource;
import java.util.List;

@Service
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "false")
public class HandbookServiceJdbcImpl implements HandbookService {
    private final JdbcTemplate jdbcTemplate;

    public HandbookServiceJdbcImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Handbook> findAll() {
        String findAllQuery = "SELECT * FROM handbook";
        return jdbcTemplate.query(findAllQuery, (rs, rowNum) -> {
            Handbook handbook = new Handbook();
            handbook.setId(rs.getInt("id"));
            handbook.setWeatherType(rs.getString("type"));
            return handbook;
        });
    }

    @Override
    public Handbook findById(Integer id) {
        String findByIdQuery = "SELECT * FROM handbook WHERE id = ?";
        return jdbcTemplate.queryForObject(findByIdQuery, (rs, rowNum) -> {
            Handbook handbook = new Handbook();
            handbook.setId(rs.getInt("id"));
            handbook.setWeatherType(rs.getString("type"));
            return handbook;
        }, id);
    }
}
