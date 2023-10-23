package org.weather.dao.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.weather.dto.HandbookDTO;
import org.weather.entity.HandbookEntity;
import org.weather.exception.handbook.HandbookAlreadyExistsException;
import org.weather.exception.handbook.HandbookTypeNotFoundException;
import org.weather.exception.sql.CitySqlException;
import org.weather.exception.sql.HandbookSqlException;
import org.weather.service.HandbookService;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.TransactionManagerHelper;
import org.weather.utils.enums.WeatherMessageEnum;

import javax.sql.DataSource;
import java.util.List;

public class HandbookServiceJdbcImpl implements HandbookService {
    private final JdbcTemplate jdbcTemplate;
    private final EntityMapper entityMapper;
    private final MessageSourceWrapper messageSourceWrapper;
    private final TransactionManagerHelper transactionManagerHelper;

    public HandbookServiceJdbcImpl(DataSource dataSource,
                                   EntityMapper entityMapper,
                                   MessageSourceWrapper messageSourceWrapper, TransactionManagerHelper transactionManagerHelper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.entityMapper = entityMapper;
        this.messageSourceWrapper = messageSourceWrapper;
        this.transactionManagerHelper = transactionManagerHelper;
    }

    @Override
    public HandbookDTO save(String weatherType) {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
            if (hasHandbookWithType(weatherType)) {
                throw new HandbookAlreadyExistsException(HttpStatus.BAD_REQUEST,
                        messageSourceWrapper.getMessageCode(WeatherMessageEnum.HANDBOOK_ALREADY_EXISTS));
            }
            String insertQuery = "INSERT INTO handbook (type) VALUES (?)";
            try {
                jdbcTemplate.update(insertQuery, weatherType);
                return findHandbookByType(weatherType);
            } catch (DataAccessException exception) {
                throw new HandbookSqlException(exception.getMessage());
            }
        });
    }

    @Override
    public HandbookDTO getOrCreateByTypeName(String weatherType) {
        if(hasHandbookWithType(weatherType)) {
            return findHandbookByType(weatherType);
        }
        return save(weatherType);
    }

    @Override
    public List<HandbookDTO> findAll() {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
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
        });
    }

    @Override
    public HandbookDTO findById(Integer id) {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
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
        });
    }

    public HandbookEntity getHandbookEntityByTypeFromRepo(String weatherType) {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
            String findHandbookByTypeQuery = "SELECT * FROM handbook WHERE type = ?";
            try {
                List<HandbookEntity> handbooks = jdbcTemplate.query(findHandbookByTypeQuery, (rs, rowNum) -> {
                    HandbookEntity handbook = new HandbookEntity();
                    handbook.setId(rs.getInt("id"));
                    handbook.setWeatherType(rs.getString("type"));
                    return handbook;
                }, weatherType);
                if (handbooks.isEmpty()) {
                    return null;
                } else {
                    return handbooks.get(0);
                }
            } catch (DataAccessException exception) {
                throw new CitySqlException(exception.getMessage());
            }
        });
    }

    public HandbookDTO mapHandbookEntityToDTO(HandbookEntity handbookEntity) {
        return entityMapper.mapHandbookEntityToDTO(handbookEntity);
    }

    public List<HandbookDTO> mapHandbookEntityListToDtoList(List<HandbookEntity> handbookEntityList) {
        return entityMapper.mapHandbookEntityListToDtoList(handbookEntityList);
    }

    private boolean hasHandbookWithType(String weatherType) {
        return getHandbookEntityByTypeFromRepo(weatherType) != null;
    }

    private HandbookEntity getHandbookEntityByTypeOrThrowException(String weatherType) {
        HandbookEntity handbook = getHandbookEntityByTypeFromRepo(weatherType);
        if(handbook == null) {
            throw new HandbookTypeNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.HANDBOOK_NOT_FOUND));
        }
        return handbook;
    }

    public HandbookDTO findHandbookByType(String weatherType) {
        return mapHandbookEntityToDTO(getHandbookEntityByTypeOrThrowException(weatherType));
    }
}
