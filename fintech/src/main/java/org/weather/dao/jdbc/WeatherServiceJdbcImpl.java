package org.weather.dao.jdbc;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.weather.dto.WeatherDTO;
import org.weather.entity.CityEntity;
import org.weather.entity.HandbookEntity;
import org.weather.entity.WeatherEntity;
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

@Service
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "false")
public class WeatherServiceJdbcImpl implements WeatherService {
    private final JdbcTemplate jdbcTemplate;
    private final CityService cityService;
    private final HandbookService handbookService;
    public WeatherServiceJdbcImpl(DataSource dataSource,
                                  @Qualifier("cityServiceJdbcImpl") @Lazy CityService cityService,
                                  @Qualifier("handbookServiceJdbcImpl")HandbookService handbookService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.cityService = cityService;
        this.handbookService = handbookService;
    }
    @Override
    public List<WeatherEntity> findAll() {
        final String findAllQuery = "SELECT * FROM weather";
        return jdbcTemplate.query(findAllQuery, (rs, rowNum) -> mapWeather(rs));
    }

    @Override
    public List<WeatherEntity> getWeatherForCity(String cityName) {
        CityEntity city = getCityByName(cityName);
        final String findAllWeatherForCityQuery = "SELECT * FROM weather WHERE city_id = ?";
        return jdbcTemplate.query(
                findAllWeatherForCityQuery,
                (rs, rowNum) -> mapWeather(rs),
                city.getId()
        );
    }

    @Override
    public WeatherEntity saveWeatherForCity(String cityName, WeatherDTO weatherDTO) {
        final String insertWeatherQuery = "INSERT INTO weather (id, temperature, city_id, date_time, handbook_id) VALUES (?, ?, ?, ?, ?)";
        HandbookEntity handbook = getHandbookById(weatherDTO.getHandbook_id());
        CityEntity city = getCityByName(cityName);
        UUID newId = UUID.randomUUID();
        jdbcTemplate.update(insertWeatherQuery, newId, weatherDTO.getTemp_val(), city.getId(),
                weatherDTO.getDateTime(), handbook.getId());

        return new WeatherEntity(newId, weatherDTO.getTemp_val(), city, weatherDTO.getDateTime(),handbook);
    }

    @Override
    public void deleteWeatherByDateTime(String cityName, WeatherDTO weatherDTO) {
        final String deleteWeatherByDateTimeQuery = "DELETE FROM weather WHERE city_id = ? AND date_time = ?";
        CityEntity city = getCityByName(cityName);
        jdbcTemplate.update(deleteWeatherByDateTimeQuery, city.getId(), weatherDTO.getDateTime());
    }

    @Override
    public WeatherEntity updateWeatherForCity(String cityName, WeatherDTO weatherDTO) {
        CityEntity city = getCityByName(cityName);
        WeatherEntity currentWeather = getWeatherByCityAndDatetime(city, weatherDTO.getDateTime());
        if(currentWeather == null) {
            return saveWeatherForCity(cityName, weatherDTO);
        }
        return updateExistingWeather(currentWeather, weatherDTO);
    }

    public WeatherEntity updateExistingWeather(WeatherEntity currentWeather, WeatherDTO weatherDTO) {
        final String updateWeatherQuery = "UPDATE weather SET temperature = ?, handbook_id = ? WHERE id = ?";
        jdbcTemplate.update(updateWeatherQuery, weatherDTO.getTemp_val(), weatherDTO.getHandbook_id(),
                currentWeather.getId());
        return new WeatherEntity(currentWeather.getId(), weatherDTO.getTemp_val(),
                currentWeather.getCity(), weatherDTO.getDateTime(),
                getHandbookById(weatherDTO.getHandbook_id())
        );
    }

    private WeatherEntity getWeatherByCityAndDatetime(CityEntity city, LocalDateTime dateTime) {
        final String getWeatherByCityAndDatetimeQuery = "SELECT * FROM weather WHERE city_id = ? AND date_time = ?";
        List<WeatherEntity> results = jdbcTemplate.query(
                getWeatherByCityAndDatetimeQuery,
                (rs, rowNum) -> mapWeather(rs),
                city.getId(), Timestamp.valueOf(dateTime)
        );
        return results.isEmpty() ? null : results.get(0);
    }

    private WeatherEntity mapWeather(ResultSet rs) throws SQLException {
        UUID id = UUID.fromString(rs.getString("id"));
        double temperature = rs.getDouble("temperature");
        LocalDateTime dateTime = rs.getTimestamp("date_time").toLocalDateTime();
        int handbook_id = rs.getInt("handbook_id");
        UUID city_id = (UUID) rs.getObject("city_id");
        return new WeatherEntity(id, temperature, getCityById(city_id),
                dateTime, getHandbookById(handbook_id));
    }

    private HandbookEntity getHandbookById(int handbook_id) {
        return handbookService.findById(handbook_id);
    }

    private CityEntity getCityByName(String cityName) {
        return cityService.findCityByName(cityName);
    }

    private CityEntity getCityById(UUID cityId) {
        return cityService.findCityById(cityId);
    }
}
