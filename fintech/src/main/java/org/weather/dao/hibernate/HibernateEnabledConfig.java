package org.weather.dao.hibernate;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.weather.cache.Cache;
import org.weather.repository.CityRepository;
import org.weather.repository.HandbookRepository;
import org.weather.repository.WeatherRepository;
import org.weather.utils.EntityMapper;
import org.weather.utils.MessageSourceWrapper;

@Configuration
@ConditionalOnProperty(value = "hibernate.enabled", havingValue = "true")
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
                                             EntityMapper entityMapper,
                                             Cache cache) {
        return new WeatherServiceImpl(weatherRepository, cityServiceImpl, handbookServiceImpl, messageSourceWrapper,
                entityMapper, cache);
    }
}
