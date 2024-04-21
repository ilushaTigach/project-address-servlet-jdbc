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
import org.telatenko.address.domain.dtos.PhoneDto;
import org.telatenko.address.domain.models.Phone;
import org.telatenko.address.domain.servlets.UserServlet;
import org.testcontainers.containers.PostgreSQLContainer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import static org.junit.jupiter.api.Assertions.*;

class PhoneServletTest {

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
        Phone phone = phoneDao.addPhone(new Phone(1, "88005553535"));

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/phones/1")).build();
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            PhoneDto body = objectMapper.readValue(responce.body(), PhoneDto.class);

            assertEquals(200, responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("88005553535", body.getNumber());

        } catch (Exception ignored){
        }
    }

    @SneakyThrows
    @Test
    void testDoPost() {
        PhoneDto phoneDto = new PhoneDto(1, "88005553535");
        String requestBody = objectMapper.writeValueAsString(phoneDto);

        try (HttpClient httpClient = HttpClient.newHttpClient()) {

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/phones"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            PhoneDto body = objectMapper.readValue(responce.body(), PhoneDto.class);

            assertEquals(200,responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("88005553535", body.getNumber());

            Phone phoneDaoAssert = phoneDao.getPhoneById(1);

            assertEquals(1, phoneDaoAssert.getId());
            assertEquals("88005553535", phoneDaoAssert.getNumber());

        } catch (Exception ignored){
        }
    }

    @SneakyThrows
    @Test
    void testDoPut(){
        Phone phone = phoneDao.addPhone(new Phone(1, "88005553535"));

        PhoneDto phoneDto = new PhoneDto(1, "88005553535");

        String requestBody = objectMapper.writeValueAsString(phoneDto);

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/phones/1"))
                    .header("Content-Type", "application/json")
                    .method("PUT", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            PhoneDto body = objectMapper.readValue(responce.body(), PhoneDto.class);

            assertEquals(200,responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("88005553535", body.getNumber());

            Phone phoneDaoAssert = phoneDao.getPhoneById(1);

            assertEquals(1, phoneDaoAssert.getId());
            assertEquals("88005553535", phoneDaoAssert.getNumber());
        } catch (Exception ignored){
        }
    }

    @SneakyThrows
    @Test
    void testDoDelete(){
        Phone phone = phoneDao.addPhone(new Phone(1, "88005553535"));

        try (HttpClient httpClient = HttpClient.newHttpClient();){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/phones/1"))
                    .method("DELETE", HttpRequest.BodyPublishers.noBody())
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, responce.statusCode());

            Phone phoneDaoAssert = phoneDao.getPhoneById(1);

            assertEquals(0, phoneDaoAssert.getId());
            assertEquals(null, phoneDaoAssert.getNumber());

        } catch (Exception ignored){
        }
    }
}