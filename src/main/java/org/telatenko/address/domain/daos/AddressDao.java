package org.telatenko.address.domain.daos;

import org.telatenko.address.domain.database.DatabaseConnector;
import org.telatenko.address.domain.models.Address;
import org.telatenko.address.domain.models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDao {

    private Connection connection;

    private PhoneDao phoneDao;

    public AddressDao(Connection connection) {
        this.connection = connection;
        this.phoneDao = new PhoneDao(connection);
    }

    public AddressDao() {
        try {
            connection = DatabaseConnector.getConnection();
            phoneDao = new PhoneDao(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Address> getAllAddresses() {
        List<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM addresses";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Address address = new Address(
                        resultSet.getInt("id"),
                        resultSet.getString("street"),
                        resultSet.getString("city"),
                        resultSet.getString("zipCode"),
                        new ArrayList<>()
                );
                address.setUsers(findUsersByAddress(address.getId()));
                addresses.add(address);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public Address getAddressById(int addressId) {
        String query = "SELECT * FROM addresses WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, addressId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Address address = new Address(
                            resultSet.getInt("id"),
                            resultSet.getString("street"),
                            resultSet.getString("city"),
                            resultSet.getString("zipCode"),
                            new ArrayList<>()
                    );
                    address.setUsers(findUsersByAddress(address.getId()));
                    return address;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Address addAddress(Address address) {
        String insertQuery = "INSERT INTO addresses (street, city, zipcode) VALUES (?, ?, ?)";
        try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStatement.setString(1, address.getStreet());
            insertStatement.setString(2, address.getCity());
            insertStatement.setString(3, address.getZipCode());
            int affectedRows = insertStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating address failed, no rows affected.");
            }
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int lastInsertId = generatedKeys.getInt(1);
                    return getAddressById(lastInsertId);
                } else {
                    throw new SQLException("Creating address failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Address updateAddress(Address address) {
        String query = "UPDATE addresses SET street = ?, city = ?, zipcode = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, address.getStreet());
            statement.setString(2, address.getCity());
            statement.setString(3, address.getZipCode());
            statement.setInt(4, address.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                return getAddressById(address.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAddress(int addressId) {
        String query = "DELETE FROM addresses WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, addressId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<User> findUsersByAddress(int addressId) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE address_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, addressId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPhone(phoneDao.getPhoneById(resultSet.getInt("phone_id")));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
