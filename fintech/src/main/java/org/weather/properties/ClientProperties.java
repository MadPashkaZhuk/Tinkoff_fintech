package org.weather.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "weather.api")
public class ClientProperties {
    @NotEmpty(message = "Please enter valid key")
    private String key;
    private String url;
}
