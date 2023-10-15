package org.weather.dao.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.weather.dto.CityDTO;
import org.weather.entity.CityEntity;
import org.weather.exception.city.CityAlreadyExistsException;
import org.weather.exception.city.CityNotFoundException;
import org.weather.service.CityService;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.enums.WeatherMessageEnum;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Service
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "false")
public class CityServiceJdbcImpl implements CityService {
    private final JdbcTemplate jdbcTemplate;
    private final MessageSourceWrapper messageSourceWrapper;
    private final WeatherServiceJdbcImpl weatherServiceJdbc;

    public CityServiceJdbcImpl(DataSource dataSource, MessageSourceWrapper messageSourceWrapper, WeatherServiceJdbcImpl weatherServiceJdbc) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.messageSourceWrapper = messageSourceWrapper;
        this.weatherServiceJdbc = weatherServiceJdbc;
    }

    @Override
    public CityEntity save(String cityName) {
        if(hasCityWithName(cityName)) {
            throw new CityAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_ALREADY_EXISTS));
        }
        String insertQuery = "INSERT INTO city (id, name) VALUES (?, ?)";
        UUID newId = UUID.randomUUID();
        jdbcTemplate.update(insertQuery, newId.toString(), cityName);
        return findCityByName(cityName);
    }

    @Override
    public void delete(String cityName) {
        if(hasCityWithName(cityName)) {
            String deleteQuery = "DELETE FROM city WHERE id = ?";
            weatherServiceJdbc.deleteAllByCityName(cityName);
            CityEntity city = findCityByName(cityName);
            jdbcTemplate.update(deleteQuery, city.getId().toString());
        }
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
        List<CityEntity> cityOrEmpty = jdbcTemplate.query(findCityByIdQuery, (rs, rowNum) -> {
            CityEntity city = new CityEntity();
            city.setId(UUID.fromString(rs.getString("id")));
            city.setName(rs.getString("name"));
            return city;
        }, id);
        if(cityOrEmpty.isEmpty()) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return cityOrEmpty.get(0);
    }

    @Override
    public CityEntity update(String cityName, CityDTO cityDTO) {
        String updateQuery = "UPDATE city SET name = ? WHERE id = ?";
        CityEntity city = getCityByNameFromRepo(cityName);
        if(hasCityWithName(cityDTO.getNewName())) {
            delete(cityName);
            return getCityByNameFromRepo(cityDTO.getNewName());
        }
        if(city == null) {
            return save(cityDTO.getNewName());
        }
        jdbcTemplate.update(updateQuery, cityDTO.getNewName(), city.getId().toString());
        city.setName(cityDTO.getNewName());
        return city;
    }

    private CityEntity getCityByNameFromRepo(String cityName) {
        String findCityByNameQuery = "SELECT * FROM city WHERE name = ?";
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
    }
    public CityEntity findCityByName(String cityName) {
        return getCitybyNameOrThrowException(cityName);
    }

    private CityEntity getCitybyNameOrThrowException(String cityName) {
        CityEntity city = getCityByNameFromRepo(cityName);
        if(city == null) {
            throw new CityNotFoundException(HttpStatus.NOT_FOUND,
                    messageSourceWrapper.getMessageCode(WeatherMessageEnum.CITY_NOT_FOUND));
        }
        return city;
    }

    private boolean hasCityWithName(String cityName) {
        return getCityByNameFromRepo(cityName) != null;
    }
}
