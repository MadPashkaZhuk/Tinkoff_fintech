package org.weather.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.weather.dao.hibernate.CityServiceImpl;
import org.weather.dao.hibernate.HandbookServiceImpl;
import org.weather.dao.hibernate.WeatherServiceImpl;
import org.weather.repository.CityRepository;
import org.weather.repository.HandbookRepository;
import org.weather.repository.WeatherRepository;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;

@Configuration
@Profile("hibernate-enabled")
public class HibernateEnabledConfig {
    @Bean
    public CityServiceImpl cityService(CityRepository cityRepository, WeatherServiceImpl weatherServiceImpl,
                                       MessageSourceWrapper messageSourceWrapper, EntityMapper entityMapper) {
        return new CityServiceImpl(cityRepository, weatherServiceImpl, messageSourceWrapper, entityMapper);
    }
    @Bean
    public HandbookServiceImpl handbookService(HandbookRepository handbookRepository,
                                               MessageSourceWrapper messageSourceWrapper,
                                               EntityMapper entityMapper) {
        return new HandbookServiceImpl(handbookRepository, messageSourceWrapper, entityMapper);
    }
    @Bean
    public WeatherServiceImpl weatherService(WeatherRepository weatherRepository,
                                             @Lazy CityServiceImpl cityServiceImpl,
                                             HandbookServiceImpl handbookServiceImpl,
                                             MessageSourceWrapper messageSourceWrapper,
                                             EntityMapper entityMapper) {
        return new WeatherServiceImpl(weatherRepository, cityServiceImpl,
                handbookServiceImpl, messageSourceWrapper, entityMapper);
    }
}
