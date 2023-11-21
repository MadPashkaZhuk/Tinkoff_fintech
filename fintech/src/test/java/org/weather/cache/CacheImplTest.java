package org.weather.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.weather.dto.CityDTO;
import org.weather.dto.HandbookDTO;
import org.weather.dto.WeatherDTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class CacheImplTest {
    @SpyBean
    CacheImpl cache;

    @BeforeEach
    public void init() {
        CacheProperties cacheProperties = new CacheProperties();
        cacheProperties.setSize(10);
        cacheProperties.setTtl(Duration.ofSeconds(1));
        cache = new CacheImpl(cacheProperties);
    }

    @Test
    public void put_ShouldSaveValue_WhenHappyPath() {
        WeatherDTO weatherDTO = new WeatherDTO(
                UUID.randomUUID(),
                15,
                LocalDateTime.MIN,
                new CityDTO(UUID.randomUUID(), "Minsk"),
                new HandbookDTO(1, "Frosty")
        );
        cache.put("Minsk", weatherDTO);
        assertEquals(Optional.of(weatherDTO), cache.get("Minsk", WeatherDTO.class));
    }

    @Test
    public void get_ShouldReturnEmpty_WhenExpired() throws Exception {
        WeatherDTO weatherDTO = new WeatherDTO(
                UUID.randomUUID(),
                15,
                LocalDateTime.MIN,
                new CityDTO(UUID.randomUUID(), "Minsk"),
                new HandbookDTO(1, "Frosty")
        );
        cache.put("Minsk", weatherDTO);
        Thread.sleep(1000);
        assertEquals(Optional.empty(), cache.get("Minsk", WeatherDTO.class));
    }

    @Test
    public void get_ShouldReturnEmpty_WhenKeyDoesntExist() {
        assertEquals(Optional.empty(), cache.get("TEST", WeatherDTO.class));
    }

    @Test
    public void put_ShouldUpdateValue_WhenSameKeyPassed() {
        WeatherDTO weatherDTO = new WeatherDTO(
                UUID.randomUUID(),
                15,
                LocalDateTime.MIN,
                new CityDTO(UUID.randomUUID(), "Minsk"),
                new HandbookDTO(1, "Frosty")
        );
        cache.put("Minsk", weatherDTO);
        WeatherDTO test = new WeatherDTO(
                UUID.randomUUID(),
                0,
                LocalDateTime.MAX,
                new CityDTO(UUID.randomUUID(), "Minsk"),
                new HandbookDTO(1, "Test")
        );
        cache.put("Minsk", test);
        assertEquals(Optional.of(test), cache.get("Minsk", WeatherDTO.class));
    }
}
