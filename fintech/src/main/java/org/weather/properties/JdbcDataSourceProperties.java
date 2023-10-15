package org.weather.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@Getter
@Setter
@ConfigurationProperties("spring.datasource")
@ConditionalOnProperty(value = "hibernate.enable", havingValue = "false")
public class JdbcDataSourceProperties {
    private String url;
    private String username;
    private String password;
    private String driverClassName;
}
