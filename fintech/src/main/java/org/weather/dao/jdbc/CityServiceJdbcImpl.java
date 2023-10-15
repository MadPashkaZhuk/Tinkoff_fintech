package org.weather.dao.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.weather.dto.CityDTO;
import org.weather.entity.CityEntity;
import org.weather.service.CityService;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "false")
public class CityServiceJdbcImpl implements CityService {
    private final JdbcTemplate jdbcTemplate;

    public CityServiceJdbcImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public CityEntity save(String cityName) {
        String insertQuery = "INSERT INTO city (id, name) VALUES (?, ?)";
        UUID newId = UUID.randomUUID();
        jdbcTemplate.update(insertQuery, newId.toString(), cityName);
        return findCityByName(cityName);
    }

    @Override
    public void delete(String cityName) {
        String deleteQuery = "DELETE FROM city WHERE id = ?";
        CityEntity city = findCityByName(cityName);
        jdbcTemplate.update(deleteQuery, city.getId().toString());
    }

    @Override
    public List<CityEntity> findAll() {
        String findAllQuery = "SELECT * FROM city";
        return jdbcTemplate.query(findAllQuery, (rs, rowNum) -> {
            CityEntity city = new CityEntity();
            city.setId(UUID.fromString(rs.getString("id")));
            city.setName(rs.getString("name"));
            return city;
        });
    }

    @Override
    public CityEntity findCityById(UUID id) {
        String findCityByIdQuery = "SELECT * FROM city WHERE id = ?";
        return jdbcTemplate.queryForObject(findCityByIdQuery, (rs, rowNum) -> {
            CityEntity city = new CityEntity();
            city.setId(UUID.fromString(rs.getString("id")));
            city.setName(rs.getString("name"));
            return city;
        }, id);
    }


    @Override
    public CityEntity update(String cityName, CityDTO cityDTO) {
        String updateQuery = "UPDATE city SET name = ? WHERE id = ?";
        CityEntity city = findCityByName(cityName);
        jdbcTemplate.update(updateQuery, cityDTO.getNewName(), city.getId().toString());
        return findCityByName(cityDTO.getNewName());
    }

    public CityEntity findCityByName(String name) {
        String findCityByNameQuery = "SELECT * FROM city WHERE name = ?";
        return jdbcTemplate.queryForObject(findCityByNameQuery, (rs, rowNum) -> {
            CityEntity city = new CityEntity();
            city.setId(UUID.fromString(rs.getString("id")));
            city.setName(rs.getString("name"));
            return city;
        }, name);
    }
}
