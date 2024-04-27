package org.telatenko.address.domain.daos;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telatenko.address.domain.models.Address;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;


    @Test
    public void testGetAddressById() throws SQLException {
        AddressDao addressDao = new AddressDao(mockConnection);
        Address address = new Address(1, "Test Street", "Test City", "12345", Arrays.asList());
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("street")).thenReturn("Test Street");
        when(mockResultSet.getString("city")).thenReturn("Test City");
        when(mockResultSet.getString("zipCode")).thenReturn("12345");

        Address getAddress = addressDao.getAddressById(1);

        assertNotNull(address);
        assertEquals(1, getAddress.getId()); // если несколько ассертов, то пихай их в assertAll
        assertEquals("Test Street", getAddress.getStreet());
        assertEquals("Test City", getAddress.getCity());
        assertEquals("12345", getAddress.getZipCode());
    }

    @Test
    public void testAddAddress() throws SQLException {
        AddressDao addressDao = new AddressDao(mockConnection);

        Address address = new Address(1, "Test Street", "Test City", "12345", Arrays.asList());

        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt(1)).thenReturn(1);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("street")).thenReturn("Test Street");
        when(mockResultSet.getString("city")).thenReturn("Test City");
        when(mockResultSet.getString("zipCode")).thenReturn("12345");

        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);


        Address addedAddress = addressDao.addAddress(address);

        assertNotNull(addedAddress);
        assertEquals(1, addedAddress.getId());
        assertEquals("Test Street", addedAddress.getStreet());
        assertEquals("Test City", addedAddress.getCity());
        assertEquals("12345", addedAddress.getZipCode());
    }

    @Test
    public void testUpdateAddress() throws SQLException {
        AddressDao addressDao = new AddressDao(mockConnection);

        Address address = new Address(1, "Test Street", "Test City", "12345", Arrays.asList());

        ResultSet mockResultSet = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true, true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("street")).thenReturn("Updated Street");
        when(mockResultSet.getString("city")).thenReturn("Updated City");
        when(mockResultSet.getString("zipCode")).thenReturn("54321");

        Address updatedAddress = addressDao.updateAddress(address);

        assertNotNull(updatedAddress);
        assertEquals(1, updatedAddress.getId());
        assertEquals("Updated Street", updatedAddress.getStreet());
        assertEquals("Updated City", updatedAddress.getCity());
        assertEquals("54321", updatedAddress.getZipCode());
    }
}