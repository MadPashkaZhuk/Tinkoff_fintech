package org.weather.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("topic.average")
public class AverageTopicProperties {
    private List<String> cities;
    private String cron;
}
