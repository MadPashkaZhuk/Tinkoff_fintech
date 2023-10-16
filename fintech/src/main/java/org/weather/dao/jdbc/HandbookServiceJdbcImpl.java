package org.weather.dao.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.weather.dto.HandbookDTO;
import org.weather.entity.HandbookEntity;
import org.weather.exception.sql.HandbookSqlException;
import org.weather.service.HandbookService;
import org.weather.utils.EntityMapper;

import javax.sql.DataSource;
import java.util.List;

public class HandbookServiceJdbcImpl implements HandbookService {
    private final JdbcTemplate jdbcTemplate;
    private final EntityMapper entityMapper;

    public HandbookServiceJdbcImpl(DataSource dataSource, EntityMapper entityMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.entityMapper = entityMapper;
    }

    @Override
    public List<HandbookDTO> findAll() {
        String findAllQuery = "SELECT * FROM handbook";
        try {
            return mapHandbookEntityListToDtoList(jdbcTemplate.query(findAllQuery, (rs, rowNum) -> {
                HandbookEntity handbook = new HandbookEntity();
                handbook.setId(rs.getInt("id"));
                handbook.setWeatherType(rs.getString("type"));
                return handbook;
            }));
        } catch (DataAccessException exception) {
            throw new HandbookSqlException(exception.getMessage());
        }
    }

    @Override
    public HandbookDTO findById(Integer id) {
        String findByIdQuery = "SELECT * FROM handbook WHERE id = ?";
        try {
            return mapHandbookEntityToDTO(jdbcTemplate.queryForObject(findByIdQuery, (rs, rowNum) -> {
                HandbookEntity handbook = new HandbookEntity();
                handbook.setId(rs.getInt("id"));
                handbook.setWeatherType(rs.getString("type"));
                return handbook;
            }, id));
        } catch (DataAccessException exception) {
            throw new HandbookSqlException(exception.getMessage());
        }
    }

    public HandbookDTO mapHandbookEntityToDTO(HandbookEntity handbookEntity) {
        return entityMapper.mapHandbookEntityToDTO(handbookEntity);
    }

    public List<HandbookDTO> mapHandbookEntityListToDtoList(List<HandbookEntity> handbookEntityList) {
        return entityMapper.mapHandbookEntityListToDtoList(handbookEntityList);
    }
}
