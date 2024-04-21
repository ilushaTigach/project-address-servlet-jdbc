package org.telatenko.address.domain.servletsIntegration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telatenko.address.domain.daos.AddressDao;
import org.telatenko.address.domain.daos.PhoneDao;
import org.telatenko.address.domain.daos.UserDao;
import org.telatenko.address.domain.database.DatabaseConnector;
import org.telatenko.address.domain.dtos.AddressDto;
import org.telatenko.address.domain.models.Address;
import org.telatenko.address.domain.servlets.UserServlet;
import org.testcontainers.containers.PostgreSQLContainer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressServletTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:16"
    );

    ObjectMapper objectMapper = new ObjectMapper();
    UserServlet userServlet;

    UserDao userDao;
    AddressDao addressDao;
    PhoneDao phoneDao;


    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        postgres.start();
        Connection databaseConnector = DatabaseConnector.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        DatabaseConnector.runLiquibase(databaseConnector);
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @SneakyThrows
    @BeforeEach
    void setUp() {
        Connection databaseConnector = DatabaseConnector.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword()
        );
        userServlet = new UserServlet(databaseConnector);
        userDao = new UserDao(databaseConnector);
        addressDao = new AddressDao(databaseConnector);
        phoneDao = new PhoneDao(databaseConnector);
        databaseConnector.prepareStatement("DELETE FROM users").executeUpdate();
        databaseConnector.prepareStatement("DELETE FROM addresses").executeUpdate();
        databaseConnector.prepareStatement("DELETE FROM phones").executeUpdate();
    }

    @SneakyThrows
    @Test
    void testDoGetById() {
        Address address = addressDao.addAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/addresses/1")).build();
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            AddressDto body = objectMapper.readValue(responce.body(), AddressDto.class);

            assertEquals(201, responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("street", body.getStreet());
            assertEquals("city", body.getCity());
            assertEquals("zipCode", body.getZipCode());
        } catch (Exception ignored){
        }
    }

    @SneakyThrows
    @Test
    void testDoPost() {
        AddressDto addressDto = new AddressDto(1, "street", "city", "zipCode", new ArrayList<>());
        String requestBody = objectMapper.writeValueAsString(addressDto);

        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/addresses"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            AddressDto body = objectMapper.readValue(responce.body(), AddressDto.class);

            assertEquals(200,responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("street", body.getStreet());
            assertEquals("city", body.getCity());
            assertEquals("zipCode", body.getZipCode());

            Address addressDaoAssert = addressDao.getAddressById(1);

            assertEquals(1, addressDaoAssert.getId());
            assertEquals("street", addressDaoAssert.getStreet());
            assertEquals("city", addressDaoAssert.getCity());
            assertEquals("zipCode", addressDaoAssert.getZipCode());
        } catch (Exception ignored){
        }
    }

    @SneakyThrows
    @Test
    void testDoPut(){
        Address address = addressDao.addAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));

        AddressDto addressDto = new AddressDto(1,"streetNew", "cityNew", "zipCodeNew", new ArrayList<>());

        String requestBody = objectMapper.writeValueAsString(addressDto);

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/addresses/1"))
                    .header("Content-Type", "application/json")
                    .method("PUT", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            AddressDto body = objectMapper.readValue(responce.body(), AddressDto.class);

            assertEquals(200, responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("street", body.getStreet());
            assertEquals("city", body.getCity());
            assertEquals("zipCode", body.getZipCode());

            Address addressDaoAssert = addressDao.getAddressById(1);

            assertEquals(1, addressDaoAssert.getId());
            assertEquals("street", addressDaoAssert.getStreet());
            assertEquals("city", addressDaoAssert.getCity());
            assertEquals("zipCode", addressDaoAssert.getZipCode());
        } catch (Exception ignored){
        }
    }

    @SneakyThrows
    @Test
    void testDoDelete(){
        Address address = addressDao.addAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));

        try (HttpClient httpClient = HttpClient.newHttpClient();){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/addresses/1"))
                    .method("DELETE", HttpRequest.BodyPublishers.noBody())
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, responce.statusCode());

            Address addressDaoAssert = addressDao.getAddressById(1);

            assertEquals(0, addressDaoAssert.getId());
            assertEquals(null, addressDaoAssert.getStreet());
            assertEquals(null, addressDaoAssert.getCity());
            assertEquals(1, addressDaoAssert.getZipCode());
        } catch (Exception ignored){
        }
    }
}