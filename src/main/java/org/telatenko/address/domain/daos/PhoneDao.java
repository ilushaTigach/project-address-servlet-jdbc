package org.telatenko.address.domain.daos;

import org.telatenko.address.domain.database.DatabaseConnector;
import org.telatenko.address.domain.models.Phone;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhoneDao {

    private Connection connection;

    public PhoneDao(Connection connection) {
        this.connection = connection;
    }
    public PhoneDao() {
        try {
            connection = DatabaseConnector.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Phone> getAllPhones() {
        List<Phone> phones = new ArrayList<>();
        String query = "SELECT * FROM phones";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Phone phone = new Phone(
                        resultSet.getInt("id"),
                        resultSet.getString("number")
                );
                phones.add(phone);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return phones;
    }

    public Phone getPhoneById(int phoneId) {
        String query = "SELECT * FROM phones WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, phoneId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Phone(
                            resultSet.getInt("id"),
                            resultSet.getString("number")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Phone addPhone(Phone phone) {
        String query = "INSERT INTO phones (number) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, phone.getNumber());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating phone failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int lastInsertId = generatedKeys.getInt(1);
                    return getPhoneById(lastInsertId);
                } else {
                    throw new SQLException("Creating phone failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Phone updatePhone(Phone phone) {
        String query = "UPDATE phones SET number = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, phone.getNumber());
            statement.setInt(2, phone.getId());
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                return getPhoneById(phone.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deletePhone(int phoneId) {
        String query = "DELETE FROM phones WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, phoneId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
