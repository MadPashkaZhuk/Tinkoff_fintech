package org.weather.dao.jdbc;

import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.NewWeatherDTO;
import org.weather.dto.WeatherDTO;
import org.weather.exception.sql.WeatherSqlException;
import org.weather.exception.weather.WeatherNotFoundException;
import org.weather.service.WeatherService;
import org.weather.utils.MessageSourceWrapper;
import org.weather.utils.TransactionManagerHelper;
import org.weather.utils.enums.WeatherMessageEnum;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class WeatherServiceJdbcImpl implements WeatherService {
    private final JdbcTemplate jdbcTemplate;
    private final CityServiceJdbcImpl cityService;
    private final HandbookServiceJdbcImpl handbookService;
    private final TransactionManagerHelper transactionManagerHelper;
    private final MessageSourceWrapper messageSourceWrapper;
    public WeatherServiceJdbcImpl(DataSource dataSource,
                                  @Lazy CityServiceJdbcImpl cityService,
                                  HandbookServiceJdbcImpl handbookService,
                                  TransactionManagerHelper transactionManagerHelper,
                                  MessageSourceWrapper messageSourceWrapper) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.cityService = cityService;
        this.handbookService = handbookService;
        this.transactionManagerHelper = transactionManagerHelper;
        this.messageSourceWrapper = messageSourceWrapper;
    }
    @Override
    public List<WeatherDTO> findAll() {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
            final String findAllQuery = "SELECT * FROM weather";
            try {
                return jdbcTemplate.query(findAllQuery, (rs, rowNum) -> mapWeather(rs));
            } catch (DataAccessException exception) {
                throw new WeatherSqlException(exception.getMessage());
            }
        });
    }

    public WeatherDTO getWeatherForCity(String cityName) {
        return getWeatherHistoryForCity(cityName).stream()
                .max(Comparator.comparing(WeatherDTO::getDateTime)).orElseThrow(() -> new WeatherNotFoundException(
                        HttpStatus.NOT_FOUND, messageSourceWrapper.getMessageCode(WeatherMessageEnum.WEATHER_NOT_FOUND)
                ));
    }

    @Override
    public List<WeatherDTO> getWeatherHistoryForCity(String cityName) {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
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
        });
    }

    @Override
    public WeatherDTO saveWeatherForCity(String cityName, NewWeatherDTO newWeatherDTO) {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
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
        });
    }

    @Override
    public void deleteWeatherByDateTime(String cityName, NewWeatherDTO newWeatherDTO) {
        transactionManagerHelper.executeInReadCommittedTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final String deleteWeatherByDateTimeQuery = "DELETE FROM weather WHERE city_id = ? AND date_time = ?";
                CityDTO city = getCityByName(cityName);
                try {
                    jdbcTemplate.update(deleteWeatherByDateTimeQuery, city.getId(), newWeatherDTO.getDateTime());
                } catch (DataAccessException exception) {
                    throw new WeatherSqlException(exception.getMessage());
                }
            }
        });
    }

    @Override
    public WeatherDTO updateWeatherForCity(String cityName, NewWeatherDTO newWeatherDTO) {
        if(cityService.getCityEntityByNameFromRepo(cityName) == null) {
            cityService.save(cityName);
        }
        CityDTO city = getCityByName(cityName);
        WeatherDTO currentWeather = getWeatherByCityAndDatetime(city, newWeatherDTO.getDateTime());
        if(currentWeather == null) {
            return saveWeatherForCity(cityName, newWeatherDTO);
        }
        return updateExistingWeather(currentWeather, newWeatherDTO);
    }

    public WeatherDTO updateExistingWeather(WeatherDTO currentWeather, NewWeatherDTO newWeatherDTO) {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
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
        });
    }

    private WeatherDTO getWeatherByCityAndDatetime(CityDTO city, LocalDateTime dateTime) {
        return transactionManagerHelper.executeInReadCommittedTransaction(status -> {
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
        });
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
        transactionManagerHelper.executeInReadCommittedTransaction(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                final String deleteWeatherByDateTimeQuery = "DELETE FROM weather WHERE city_id = ?";
                CityDTO city = getCityByName(cityName);
                try {
                    jdbcTemplate.update(deleteWeatherByDateTimeQuery, city.getId());
                } catch (DataAccessException exception) {
                    throw new WeatherSqlException(exception.getMessage());
                }
            }
        });
    }

    private HandbookDTO getHandbookDTOById(Integer id) {
        return handbookService.findById(id);
    }
}
