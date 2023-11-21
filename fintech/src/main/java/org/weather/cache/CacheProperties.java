package org.weather.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "cache.course")
public class CacheProperties {
    private int size;
    private Duration ttl;
}
