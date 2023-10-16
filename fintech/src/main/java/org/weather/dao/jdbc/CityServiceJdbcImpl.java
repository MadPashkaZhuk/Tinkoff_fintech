package org.weather.dao.jdbc;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.weather.dto.CityDTO;
import org.weather.dto.NewCityDTO;
import org.weather.entity.CityEntity;
import org.weather.exception.city.CityAlreadyExistsException;
import org.weather.exception.city.CityNotFoundException;
import org.weather.exception.sql.CitySqlException;
import org.weather.service.CityService;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

public class CityServiceJdbcImpl implements CityService {
    private final JdbcTemplate jdbcTemplate;
    private final MessageSourceWrapper messageSourceWrapper;
    private final WeatherServiceJdbcImpl weatherServiceJdbc;
    private final EntityMapper entityMapper;

    public CityServiceJdbcImpl(DataSource dataSource, MessageSourceWrapper messageSourceWrapper, WeatherServiceJdbcImpl weatherServiceJdbc, EntityMapper entityMapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.messageSourceWrapper = messageSourceWrapper;
        this.weatherServiceJdbc = weatherServiceJdbc;
        this.entityMapper = entityMapper;
    }

    @Override
    public CityDTO save(String cityName) {
        if(hasCityWithName(cityName)) {
            throw new CityAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_ALREADY_EXISTS));
        }
        String insertQuery = "INSERT INTO city (id, name) VALUES (?, ?)";
        UUID newId = UUID.randomUUID();
        try {
            jdbcTemplate.update(insertQuery, newId.toString(), cityName);
            return findCityByName(cityName);
        } catch (DataAccessException exception) {
            throw new CitySqlException(exception.getMessage());
        }
    }

    @Override
    public void delete(String cityName) {
        if(!hasCityWithName(cityName)){
            return;
        }
        String deleteQuery = "DELETE FROM city WHERE id = ?";
        weatherServiceJdbc.deleteAllByCityName(cityName);
        CityDTO city = findCityByName(cityName);
        try {
            jdbcTemplate.update(deleteQuery, city.getId().toString());
        } catch (DataAccessException exception) {
            throw new CitySqlException(exception.getMessage());
        }
    }

    @Override
    public List<CityDTO> findAll() {
        String findAllQuery = "SELECT * FROM city";
        try {
            return mapCityEntityListToDtoList(jdbcTemplate.query(findAllQuery, (rs, rowNum) -> {
                CityEntity city = new CityEntity();
                city.setId(UUID.fromString(rs.getString("id")));
                city.setName(rs.getString("name"));
                return city;
            }));
        } catch (DataAccessException exception) {
            throw new CitySqlException(exception.getMessage());
        }
    }

    @Override
    public CityDTO findCityById(UUID id) {
        String findCityByIdQuery = "SELECT * FROM city WHERE id = ?";
        try {
            List<CityEntity> cityOrEmpty = jdbcTemplate.query(findCityByIdQuery, (rs, rowNum) -> {
                CityEntity city = new CityEntity();
                city.setId(UUID.fromString(rs.getString("id")));
                city.setName(rs.getString("name"));
                return city;
            }, id);
            if (cityOrEmpty.isEmpty()) {
                throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                        messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
            }
            return mapCityEntityToDTO(cityOrEmpty.get(0));
        } catch (DataAccessException exception) {
            throw new CitySqlException(exception.getMessage());
        }
    }

    @Override
    public CityDTO update(String cityName, NewCityDTO newCityDTO) {
        String updateQuery = "UPDATE city SET name = ? WHERE id = ?";
        CityEntity city = getCityEntityByNameFromRepo(cityName);
        if(hasCityWithName(newCityDTO.getNewName())) {
            delete(cityName);
            return mapCityEntityToDTO(getCityEntityByNameFromRepo(newCityDTO.getNewName()));
        }
        if(city == null) {
            return save(newCityDTO.getNewName());
        }
        try {
            jdbcTemplate.update(updateQuery, newCityDTO.getNewName(), city.getId().toString());
            city.setName(newCityDTO.getNewName());
            return mapCityEntityToDTO(city);
        } catch (DataAccessException exception) {
            throw new CitySqlException(exception.getMessage());
        }
    }

    private CityEntity getCityEntityByNameFromRepo(String cityName) {
        String findCityByNameQuery = "SELECT * FROM city WHERE name = ?";
        try {
            List<CityEntity> cities = jdbcTemplate.query(findCityByNameQuery, (rs, rowNum) -> {
                CityEntity city = new CityEntity();
                city.setId(UUID.fromString(rs.getString("id")));
                city.setName(rs.getString("name"));
                return city;
            }, cityName);
            if (cities.isEmpty()) {
                return null;
            } else {
                return cities.get(0);
            }
        } catch (DataAccessException exception) {
            throw new CitySqlException(exception.getMessage());
        }
    }

    public CityDTO findCityByName(String cityName) {
        return mapCityEntityToDTO(getCityEntityByNameOrThrowException(cityName));
    }

    private CityEntity getCityEntityByNameOrThrowException(String cityName) {
        CityEntity city = getCityEntityByNameFromRepo(cityName);
        if(city == null) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return city;
    }

    private boolean hasCityWithName(String cityName) {
        return getCityEntityByNameFromRepo(cityName) != null;
    }

    private CityDTO mapCityEntityToDTO(CityEntity city) {
        return entityMapper.mapCityEntityToDTO(city);
    }

    private List<CityDTO> mapCityEntityListToDtoList(List<CityEntity> cityEntityList) {
        return entityMapper.mapCityEntityListToDtoList(cityEntityList);
    }
}
