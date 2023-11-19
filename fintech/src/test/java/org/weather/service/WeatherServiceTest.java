package org.weather.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.weather.cache.WeatherCache;
import org.weather.dto.NewWeatherDTO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class WeatherServiceTest {
    @Autowired
    @SpyBean
    WeatherService weatherService;
    @SpyBean
    WeatherCache weatherCache;
    @Autowired
    DataSource dataSource;
    @Container
    public static GenericContainer h2 = new GenericContainer(DockerImageName.parse("oscarfonts/h2"))
            .withExposedPorts(1521, 81)
            .withEnv("H2_OPTIONS", "-ifNotExists")
            .waitingFor(Wait.defaultWaitStrategy());

    @DynamicPropertySource
    static void setPropertySource(DynamicPropertyRegistry dynamicPropertySource) {
        dynamicPropertySource.add("spring.datasource.url",
                () -> String.format("jdbc:h2:tcp://localhost:%d/test", h2.getMappedPort(1521)));
    }

    @BeforeAll
    public static void setup() {
        h2.start();
    }

    @BeforeEach
    public void clearTables() throws Exception {
        Connection connection = dataSource.getConnection();
        PreparedStatement weatherPreparedStatement = connection.prepareStatement("DELETE FROM weather");
        weatherPreparedStatement.execute();
        connection.close();
    }

    @AfterAll
    public static void tearDown() {
        h2.stop();
    }

    @Test
    public void getWeatherForCity_ShouldAddDataToCache_WhenNotCached() {
        weatherService.saveWeatherForCity("Minsk", new NewWeatherDTO(
                13, LocalDateTime.now(), 1
        ));
        weatherService.getWeatherForCity("Minsk");
        Mockito.verify(weatherCache, Mockito.times(1)).getWeather(Mockito.any());
        assertTrue(weatherCache.getWeather("Minsk").isPresent());
    }

    @Test
    public void getWeatherForCity_ShouldTakeFromCacheAndGoToDatabaseOnce_WhenSecondCall() {
        weatherService.saveWeatherForCity("Minsk", new NewWeatherDTO(
                13, LocalDateTime.now(), 1
        ));
        weatherService.getWeatherForCity("Minsk");
        Mockito.verify(weatherService, Mockito.times(0)).getWeatherHistoryForCity(Mockito.any());
        Mockito.verify(weatherCache, Mockito.times(1)).updateWeather(Mockito.any());
    }

    @Test
    public void saveWeatherForCity_ShouldCacheData_WhenNewDataIsSaved() {
        weatherService.saveWeatherForCity("Minsk", new NewWeatherDTO(
                13, LocalDateTime.now(), 1
        ));
        Mockito.verify(weatherCache, Mockito.times(1)).updateWeather(Mockito.any());
    }

    @Test
    public void saveWeatherForCity_ShouldUpdateExistingData_IfDataWasAlreadyCached() {
        weatherService.saveWeatherForCity("Minsk", new NewWeatherDTO(
                13, LocalDateTime.MIN, 1
        ));
        weatherService.saveWeatherForCity("Minsk", new NewWeatherDTO(
                50, LocalDateTime.now(), 3
        ));
        Mockito.verify(weatherCache, Mockito.times(2)).updateWeather(Mockito.any());
        assertEquals(50, weatherCache.getWeather("Minsk").get().getTemperature());
    }

    @Test
    public void updateWeatherForCity_ShouldUpdateExistingData_IfDataWasAlreadyCached() {
        weatherService.saveWeatherForCity("Minsk", new NewWeatherDTO(
                13, LocalDateTime.MIN, 1
        ));
        weatherService.updateWeatherForCity("Minsk", new NewWeatherDTO(
                20, LocalDateTime.MAX, 3
        ));
        Mockito.verify(weatherCache, Mockito.times(2)).updateWeather(Mockito.any());
        assertEquals(20, weatherCache.getWeather("Minsk").get().getTemperature());
    }
}
