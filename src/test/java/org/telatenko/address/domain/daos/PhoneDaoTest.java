package org.telatenko.address.domain.daos;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telatenko.address.domain.models.Phone;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PhoneDaoTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @SneakyThrows
    @Test
    void testGetPhoneById() {
        ResultSet mockResultSet = mock(ResultSet.class);
        PhoneDao phoneDao = new PhoneDao(mockConnection);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("number")).thenReturn("1234567890");

        Phone phone = phoneDao.getPhoneById(1);
        assertNotNull(phone);
        assertEquals(1, phone.getId());
        assertEquals("1234567890", phone.getNumber());
    }

    @SneakyThrows
    @Test
    void testAddPhone(){
        PhoneDao phoneDao = new PhoneDao(mockConnection);
        Phone phone = new Phone(1, "1234567890");
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockResultSet.next()).thenReturn(true,true);
        when(mockResultSet.getInt(1)).thenReturn(1);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("number")).thenReturn("1234567890");
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        Phone addedPhone = phoneDao.addPhone(phone);

        assertNotNull(addedPhone);
        assertEquals(1, addedPhone.getId());
        assertEquals("1234567890", addedPhone.getNumber());
    }

    @SneakyThrows
    @Test
    void testUpdatePhone(){
        PhoneDao phoneDao = new PhoneDao(mockConnection);
        ResultSet mockResultSet = mock(ResultSet.class);
        Phone phone = new Phone(1, "1234567890");

        when(mockResultSet.next()).thenReturn(true,true);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("number")).thenReturn("1234567890");
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);

        Phone updatedPhone = phoneDao.updatePhone(phone);

        assertNotNull(updatedPhone);
        assertEquals(1, updatedPhone.getId());
        assertEquals("1234567890", updatedPhone.getNumber());
    }
}