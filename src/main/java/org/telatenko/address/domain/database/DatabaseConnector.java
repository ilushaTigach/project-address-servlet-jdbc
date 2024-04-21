package org.telatenko.address.domain.database;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:postgresql://localhost:5432/address";
    private static final String USER = "root";
    private static final String PASSWORD = "123";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void runLiquibase(Connection connection) {
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db.changelog/changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void runLiquibase() {
        try {
            Connection connection = getConnection();
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase("db.changelog/changelog-master.yaml", new ClassLoaderResourceAccessor(), database);
            liquibase.update("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}