package org.weather.dao.jdbc;

import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.NewWeatherDTO;
import org.weather.dto.WeatherDTO;
import org.weather.exception.sql.WeatherSqlException;
import org.weather.service.CityService;
import org.weather.service.HandbookService;
import org.weather.service.WeatherService;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WeatherServiceJdbcImpl implements WeatherService {
    private final JdbcTemplate jdbcTemplate;
    private final CityService cityService;
    private final HandbookService handbookService;
    public WeatherServiceJdbcImpl(DataSource dataSource, @Lazy CityService cityService,
                                  HandbookService handbookService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.cityService = cityService;
        this.handbookService = handbookService;
    }
    @Override
    public List<WeatherDTO> findAll() {
        final String findAllQuery = "SELECT * FROM weather";
        try {
            return jdbcTemplate.query(findAllQuery, (rs, rowNum) -> mapWeather(rs));
        } catch (DataAccessException exception) {
            throw new WeatherSqlException(exception.getMessage());
        }
    }

    @Override
    public List<WeatherDTO> getWeatherForCity(String cityName) {
        CityDTO city = getCityByName(cityName);
        final String findAllWeatherForCityQuery = "SELECT * FROM weather WHERE city_id = ?";
        try {
            return jdbcTemplate.query(
                    findAllWeatherForCityQuery,
                    (rs, rowNum) -> mapWeather(rs),
                    city.getId()
            );
        } catch (DataAccessException exception) {
            throw new WeatherSqlException(exception.getMessage());
        }
    }

    @Override
    public WeatherDTO saveWeatherForCity(String cityName, NewWeatherDTO newWeatherDTO) {
        final String insertWeatherQuery = "INSERT INTO weather (id, temperature, city_id, date_time, handbook_id) VALUES (?, ?, ?, ?, ?)";
        HandbookDTO handbook = getHandbookDTOById(newWeatherDTO.getHandbook_id());
        CityDTO city = getCityByName(cityName);
        UUID newId = UUID.randomUUID();
        try {
            jdbcTemplate.update(insertWeatherQuery, newId, newWeatherDTO.getTemp_val(), city.getId(),
                    newWeatherDTO.getDateTime(), handbook.getId());
            return new WeatherDTO(newId, newWeatherDTO.getTemp_val(), newWeatherDTO.getDateTime(),
                    city, handbook);
        } catch (DataAccessException exception) {
            throw new WeatherSqlException(exception.getMessage());
        }
    }

    @Override
    public void deleteWeatherByDateTime(String cityName, NewWeatherDTO newWeatherDTO) {
        final String deleteWeatherByDateTimeQuery = "DELETE FROM weather WHERE city_id = ? AND date_time = ?";
        CityDTO city = getCityByName(cityName);
        try {
            jdbcTemplate.update(deleteWeatherByDateTimeQuery, city.getId(), newWeatherDTO.getDateTime());
        } catch (DataAccessException exception) {
            throw new WeatherSqlException(exception.getMessage());
        }
    }

    @Override
    public WeatherDTO updateWeatherForCity(String cityName, NewWeatherDTO newWeatherDTO) {
        CityDTO city = getCityByName(cityName);
        WeatherDTO currentWeather = getWeatherByCityAndDatetime(city, newWeatherDTO.getDateTime());
        if(currentWeather == null) {
            return saveWeatherForCity(cityName, newWeatherDTO);
        }
        return updateExistingWeather(currentWeather, newWeatherDTO);
    }

    public WeatherDTO updateExistingWeather(WeatherDTO currentWeather, NewWeatherDTO newWeatherDTO) {
        final String updateWeatherQuery = "UPDATE weather SET temperature = ?, handbook_id = ? WHERE id = ?";
        try {
            jdbcTemplate.update(updateWeatherQuery, newWeatherDTO.getTemp_val(), newWeatherDTO.getHandbook_id(),
                    currentWeather.getId());
            return new WeatherDTO(currentWeather.getId(), newWeatherDTO.getTemp_val(),
                    newWeatherDTO.getDateTime(), currentWeather.getCity(),
                    getHandbookDTOById(newWeatherDTO.getHandbook_id())
            );
        } catch (DataAccessException exception) {
            throw new WeatherSqlException(exception.getMessage());
        }
    }

    private WeatherDTO getWeatherByCityAndDatetime(CityDTO city, LocalDateTime dateTime) {
        final String getWeatherByCityAndDatetimeQuery = "SELECT * FROM weather WHERE city_id = ? AND date_time = ?";
        try {
            List<WeatherDTO> results = jdbcTemplate.query(
                    getWeatherByCityAndDatetimeQuery,
                    (rs, rowNum) -> mapWeather(rs),
                    city.getId(), Timestamp.valueOf(dateTime)
            );
            return results.isEmpty() ? null : results.get(0);
        } catch (DataAccessException exception) {
            throw new WeatherSqlException(exception.getMessage());
        }
    }

    private WeatherDTO mapWeather(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        double temperature = rs.getDouble("temperature");
        LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
        int handbook_id = rs.getInt("handbook_id");
        UUID city_id = (UUID) rs.getObject("city_id");
        return new WeatherDTO(id, temperature, dateTime, getCityById(city_id), getHandbookDTOById(handbook_id));
    }

    private CityDTO getCityByName(String cityName) {
        return cityService.findCityByName(cityName);
    }

    private CityDTO getCityById(UUID cityId) {
        return cityService.findCityById(cityId);
    }

    public void deleteAllByCityName(String cityName) {
        final String deleteWeatherByDateTimeQuery = "DELETE FROM weather WHERE city_id = ?";
        CityDTO city = getCityByName(cityName);
        try {
            jdbcTemplate.update(deleteWeatherByDateTimeQuery, city.getId());
        } catch (DataAccessException exception) {
            throw new WeatherSqlException(exception.getMessage());
        }
    }

    private HandbookDTO getHandbookDTOById(Integer id) {
        return handbookService.findById(id);
    }
}
