package org.weather.dao.jdbc;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;

import javax.sql.DataSource;

@Configuration
@ConditionalOnProperty(value = "hibernate.enabled", havingValue = "false")
public class HibernateDisabledConfig {
    @Bean
    public CityServiceJdbcImpl cityServiceJdbcImplHibernateDisabled(DataSource dataSource,
                                                            MessageSourceWrapper messageSourceWrapper,
                                                            WeatherServiceJdbcImpl weatherServiceJdbc,
                                                            EntityMapper entityMapper) {
        return new CityServiceJdbcImpl(dataSource, messageSourceWrapper, weatherServiceJdbc, entityMapper);
    }
    @Bean
    public HandbookServiceJdbcImpl handbookServiceHibernateDisabled(DataSource dataSource,
                                                            EntityMapper entityMapper) {
        return new HandbookServiceJdbcImpl(dataSource, entityMapper);
    }
    @Bean
    public WeatherServiceJdbcImpl weatherServiceHibernateDisabled(DataSource dataSource,
                                                                  @Lazy CityServiceJdbcImpl cityService,
                                                                  HandbookServiceJdbcImpl handbookService) {
        return new WeatherServiceJdbcImpl(dataSource, cityService, handbookService);
    }
}
