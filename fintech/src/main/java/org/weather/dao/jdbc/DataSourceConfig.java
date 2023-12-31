package org.weather.dao.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class DataSourceConfig {
    private final JdbcDataSourceProperties jdbcDataSourceProperties;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcDataSourceProperties.getUrl());
        config.setUsername(jdbcDataSourceProperties.getUsername());
        config.setPassword(jdbcDataSourceProperties.getPassword());
        config.setDriverClassName(jdbcDataSourceProperties.getDriverClassName());
        return new HikariDataSource(config);
    }
}
