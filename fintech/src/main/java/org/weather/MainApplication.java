package org.weather;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.weather.cache.CacheProperties;
import org.weather.client.ClientProperties;
import org.weather.dao.jdbc.JdbcDataSourceProperties;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Weather API", version = "1.0"))
@EnableConfigurationProperties({CacheProperties.class, JdbcDataSourceProperties.class, ClientProperties.class})
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
