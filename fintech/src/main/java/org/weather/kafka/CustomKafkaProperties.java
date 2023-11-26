package org.weather.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("spring.kafka")
public class CustomKafkaProperties {
    private String bootstrapServers;
    private String topic;
}
