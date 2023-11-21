package org.weather.dao.jdbc;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@Getter
@Setter
@ConfigurationProperties("spring.datasource")
public class JdbcDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
