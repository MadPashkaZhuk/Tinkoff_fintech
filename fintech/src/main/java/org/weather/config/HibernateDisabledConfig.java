package org.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.weather.dao.jdbc.CityServiceJdbcImpl;
import org.weather.dao.jdbc.HandbookServiceJdbcImpl;
import org.weather.dao.jdbc.WeatherServiceJdbcImpl;
import org.weather.service.CityService;
import org.weather.service.HandbookService;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;

import javax.sql.DataSource;

@Configuration
@Profile("hibernate-disabled")
public class HibernateDisabledConfig {
    @Bean
    public CityService cityServiceJdbcImplHibernateDisabled(DataSource dataSource,
                                                            MessageSourceWrapper messageSourceWrapper,
                                                            WeatherServiceJdbcImpl weatherServiceJdbc,
                                                            EntityMapper entityMapper) {
        return new CityServiceJdbcImpl(dataSource, messageSourceWrapper, weatherServiceJdbc, entityMapper);
    }
    @Bean
    public HandbookService handbookServiceHibernateDisabled(DataSource dataSource,
                                                            EntityMapper entityMapper) {
        return new HandbookServiceJdbcImpl(dataSource, entityMapper);
    }
    @Bean
    public WeatherServiceJdbcImpl weatherServiceHibernateDisabled(DataSource dataSource,
                                                                  @Lazy CityService cityService,
                                                                  HandbookService handbookService) {
        return new WeatherServiceJdbcImpl(dataSource, cityService, handbookService);
    }
}
