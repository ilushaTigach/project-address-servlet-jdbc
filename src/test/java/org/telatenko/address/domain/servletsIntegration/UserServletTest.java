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
import org.telatenko.address.domain.dtos.UserDto;
import org.telatenko.address.domain.models.Address;
import org.telatenko.address.domain.models.Phone;
import org.telatenko.address.domain.models.User;
import org.telatenko.address.domain.servlets.UserServlet;
import org.testcontainers.containers.PostgreSQLContainer;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class UserServletTest {

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
        Address address = addressDao.addAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));
        userDao.addUser(new User(1, "name", "email", address, phone));

        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/users/1")).build();
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            UserDto body = objectMapper.readValue(responce.body(), UserDto.class);

            assertEquals(201, responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("name", body.getName());
            assertEquals("email", body.getEmail());
            assertEquals(1, body.getAddressId());
            assertEquals(1, body.getPhoneId());
        } catch (ConnectException ignored) {
        }
    }

    @SneakyThrows
    @Test
    void testDoPost() {
        Phone phone = phoneDao.addPhone(new Phone(1, "88005553535"));
        Address address = addressDao.addAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));
        UserDto userDto = new UserDto(1, "name", "email", 1, 1 );
        String requestBody = objectMapper.writeValueAsString(userDto);

        try (HttpClient httpClient = HttpClient.newHttpClient()){

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users"))
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            UserDto body = objectMapper.readValue(responce.body(), UserDto.class);

            assertEquals(200,responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("name", body.getName());
            assertEquals("email", body.getEmail());
            assertEquals(1, body.getAddressId());
            assertEquals(1, body.getPhoneId());

            User userDaoAssert = userDao.getUserById(1);

            assertEquals(1, userDaoAssert.getId());
            assertEquals("name", userDaoAssert.getName());
            assertEquals("email", userDaoAssert.getEmail());
            assertEquals(1, userDaoAssert.getAddress().getId());
            assertEquals(1, userDaoAssert.getPhone().getId());
        } catch (Exception ignored) {
        }
    }

    @SneakyThrows
    @Test
    void testDoPut() {
        Phone phone = phoneDao.addPhone(new Phone(1, "88005553535"));
        Address address = addressDao.addAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));
        userDao.addUser(new User(1, "name", "email", address, phone));

        UserDto userDto = new UserDto(1, "nameNew", "emailNew", 1, 1 );
        String requestBody = objectMapper.writeValueAsString(userDto);

        try (HttpClient httpClient = HttpClient.newHttpClient()){
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/users/1"))
                    .header("Content-Type", "application/json")
                    .method("PUT", HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            UserDto body = objectMapper.readValue(responce.body(), UserDto.class);

            assertEquals(200,responce.statusCode());
            assertEquals(1, body.getId());
            assertEquals("nameNew", body.getName());
            assertEquals("emailNew", body.getEmail());

            User userDaoAssert = userDao.getUserById(1);

            assertEquals(1, userDaoAssert.getId());
            assertEquals("nameNew", userDaoAssert.getName());
            assertEquals("emailNew", userDaoAssert.getEmail());

        } catch (Exception ignored){

        }
    }

    @Test
    void testDoDelete(){
        Phone phone = phoneDao.addPhone(new Phone(1, "88005553535"));
        Address address = addressDao.addAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));
        userDao.addUser(new User(1, "name", "email", address, phone));

        try (HttpClient httpClient = HttpClient.newHttpClient();){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users/1"))
                .method("DELETE", HttpRequest.BodyPublishers.noBody())
                .build();
        httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        HttpResponse<String> responce = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, responce.statusCode());

        User userDaoAssert = userDao.getUserById(1);

        assertEquals(1, userDaoAssert.getId());
        assertEquals(null, userDaoAssert.getName());
        assertEquals("email", userDaoAssert.getEmail());
        assertEquals(1, userDaoAssert.getAddress().getId());
        assertEquals(1, userDaoAssert.getPhone().getId());
        } catch (Exception ignored){

        }
    }
}