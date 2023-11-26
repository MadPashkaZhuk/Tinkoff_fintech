package org.weather.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.weather.dto.NewWeatherDTO;

@RequiredArgsConstructor
@Component
public class WeatherProducer {
    private final KafkaTemplate<String, NewWeatherDTO> kafkaTemplate;
    private final CustomKafkaProperties kafkaProperties;
    public void sendWeatherDto(String cityName, NewWeatherDTO newWeatherDTO) {
        kafkaTemplate.send(kafkaProperties.getTopic(), cityName, newWeatherDTO);
    }
}
