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
import org.weather.dto.CityDTO;
import org.weather.dto.NewCityDTO;
import org.weather.exception.city.CityAlreadyExistsException;
import org.weather.exception.city.CityNotFoundException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class CityServiceTest {
    @Autowired
    @SpyBean
    CityService cityService;
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
        PreparedStatement cityPreparedStatement = connection.prepareStatement("DELETE FROM city");
        cityPreparedStatement.execute();
        connection.close();
    }

    @AfterAll
    public static void tearDown() {
        h2.stop();
    }

    @Test
    public void findAll_ShouldReturnCityList_WhenDataExists() throws Exception {
        cityService.save("Minsk");
        cityService.save("Brest");
        Connection connection = dataSource.getConnection();
        List<CityDTO> cities = cityService.findAll();
        String query = "SELECT * FROM city";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            CityDTO cityDTO = new CityDTO(UUID.fromString(resultSet.getString("id")),
                    resultSet.getString("name"));
            assertTrue(cities.contains(cityDTO));
        }
        assertEquals(2, cities.size());
    }

    @Test
    public void save_ShouldSaveCity_WhenCityDoesntExists() throws Exception {
        String regionName = "London";
        cityService.save(regionName);
        String query = "SELECT * FROM city where name = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, regionName);
        ResultSet resultSet = preparedStatement.executeQuery();
        int counter = 0;
        while (resultSet.next()) {
            counter++;
            assertEquals(resultSet.getString("name"), regionName);
        }
        assertEquals(1, counter);
    }

    @Test
    public void save_ShouldThrowCityAlreadyExistsException_WhenCityExists() {
        String regionName = "Minsk";
        cityService.save(regionName);
        assertThrows(CityAlreadyExistsException.class, () -> cityService.save(regionName));
    }

    @Test
    public void delete_ShouldDeleteCity_WhenCityExists() throws Exception {
        String regionName = "Brest";
        cityService.delete(regionName);
        Connection connection = dataSource.getConnection();
        String query = "SELECT COUNT(id) FROM city where name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, regionName);
        cityService.delete(regionName);
        ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
        resultSetAfterDeletion.next();
        int countAfterDeletion = resultSetAfterDeletion.getInt(1);
        assertEquals(0, countAfterDeletion);
    }

    @Test
    public void delete_ShouldDoNothing_WhenCityDoesntExist() throws Exception {
        String regionName = "TEST";
        Connection connection = dataSource.getConnection();
        String query = "SELECT COUNT(name) FROM city";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        cityService.delete(regionName);
        ResultSet resultSetAfterDeletion = preparedStatement.executeQuery();
        resultSetAfterDeletion.next();
        int countAfterDeletion = resultSetAfterDeletion.getInt(1);
        assertEquals(0, countAfterDeletion);
    }

    @Test
    public void findCityByName_ShouldReturnCityDTO_WhenCityExists() throws Exception {
        String regionName = "Minsk";
        cityService.save(regionName);
        CityDTO expectedDTO = cityService.findCityByName(regionName);
        String query = "SELECT * FROM city WHERE name = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, regionName);
        ResultSet resultSet = preparedStatement.executeQuery();
        CityDTO actual = null;
        while(resultSet.next()) {
            actual = new CityDTO(UUID.fromString(resultSet.getString("id")),
                    resultSet.getString("name"));
        }
        assertEquals(actual, expectedDTO);
    }

    @Test
    public void findCityByName_ShouldThrowCityNotFoundException_WhenCityDoesntExist() {
        String regionName = "TEST";
        assertThrows(CityNotFoundException.class, () -> cityService.findCityByName(regionName));
    }

    @Test
    public void findCityById_ShouldReturnCityDTO_WhenCityExists() throws Exception {
        String regionName = "Minsk";
        cityService.save(regionName);
        CityDTO expectedDTO = cityService.findCityByName(regionName);
        UUID expectedId = expectedDTO.getId();
        String query = "SELECT * FROM city WHERE id = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, expectedId.toString());
        ResultSet resultSet = preparedStatement.executeQuery();
        CityDTO actual = null;
        while(resultSet.next()) {
            actual = new CityDTO(UUID.fromString(resultSet.getString("id")),
                    resultSet.getString("name"));
        }
        assertEquals(actual, expectedDTO);
    }

    @Test
    public void findCityById_ShouldThrowCityNotFoundException_WhenCityDoesntExist() {
        UUID id = UUID.randomUUID();
        assertThrows(CityNotFoundException.class, () -> cityService.findCityById(id));
    }

    @Test
    public void update_ShouldUpdateCityName_WhenCityAlreadyExists() throws Exception {
        String regionNameToSave = "Minsk";
        String newRegionName = "Brest";
        cityService.save(regionNameToSave);
        cityService.update(regionNameToSave, new NewCityDTO(newRegionName));
        String query = "SELECT COUNT(name) FROM city WHERE name = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, newRegionName);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int counter = resultSet.getInt(1);
        assertEquals(1, counter);
    }

    @Test
    public void update_ShouldSaveCity_WhenCityDoesntExist() throws Exception {
        String nonExistingCity = "Minsk";
        String newRegionName = "Brest";
        cityService.update(nonExistingCity, new NewCityDTO(newRegionName));
        String query = "SELECT COUNT(name) FROM city WHERE name = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, newRegionName);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int counter = resultSet.getInt(1);
        assertEquals(1, counter);
        Mockito.verify(cityService, Mockito.times(1)).save(newRegionName);
    }
}
