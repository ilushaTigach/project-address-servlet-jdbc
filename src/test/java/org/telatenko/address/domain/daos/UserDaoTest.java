package org.telatenko.address.domain.daos;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telatenko.address.domain.models.Address;
import org.telatenko.address.domain.models.Phone;
import org.telatenko.address.domain.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class UserDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @SneakyThrows
    @Test
    void testGetUserById() {
        AddressDao mockAddressDao = mock(AddressDao.class);
        PhoneDao mockPhoneDao = mock(PhoneDao.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        UserDao userDao = new UserDao(mockConnection);

        User testUser = new User();
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));
        testUser.setPhone(new Phone(1, "1234567890"));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test User");
        when(mockResultSet.getString("email")).thenReturn("test@example.com");
        when(mockResultSet.getInt("address_id")).thenReturn(1);
        when(mockResultSet.getInt("phone_id")).thenReturn(1);

        when(mockAddressDao.getAddressById(anyInt())).thenReturn(new Address());
        when(mockPhoneDao.getPhoneById(anyInt())).thenReturn(new Phone());

        User getUser = userDao.getUserById(1);

        assertNotNull(getUser);
        assertEquals(1, getUser.getId());
        assertEquals("Test User", getUser.getName());
        assertEquals("test@example.com", getUser.getEmail());
        assertNotNull(getUser.getAddress());
        assertNotNull(getUser.getPhone());
    }

    @SneakyThrows
    @Test
    void  testAddUser(){
        ResultSet mockResultSet = mock(ResultSet.class);

        UserDao userDao = new UserDao(mockConnection);

        User testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setAddress(new Address(1, "street", "city", "zipCode", new ArrayList<>()));
        testUser.setPhone(new Phone(1, "1234567890"));

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false,true);
        when(mockResultSet.getInt(1)).thenReturn(1);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test User");
        when(mockResultSet.getString("email")).thenReturn("test@example.com");
        when(mockResultSet.getInt("address_id")).thenReturn(1);
        when(mockResultSet.getInt("phone_id")).thenReturn(1);

        User addedUser = userDao.addUser(testUser);

        assertNotNull(addedUser);
        assertEquals(1, addedUser.getId());
        assertEquals("Test User", addedUser.getName());
        assertEquals("test@example.com", addedUser.getEmail());
        assertNotNull(addedUser.getAddress());
        assertNotNull(addedUser.getPhone());
    }

    @SneakyThrows
    @Test
    void testUpdateUser() {
        ResultSet mockResultSet = mock(ResultSet.class);
        UserDao userDao = new UserDao(mockConnection);

        User testUser = new User();
        testUser.setId(1);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Test User");
        when(mockResultSet.getString("email")).thenReturn("test@example.com");

        User updateUser = userDao.updateUser(testUser);

        assertNotNull(updateUser);
        assertEquals(1, updateUser.getId());
        assertEquals("Test User", updateUser.getName());
        assertEquals("test@example.com", updateUser.getEmail());
    }
}