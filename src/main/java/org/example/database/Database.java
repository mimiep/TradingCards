package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mtcg";
    private static final String DB_USER = "mtcg_user"; // Dein DB Benutzername
    private static final String DB_PASSWORD = "audi80"; // Dein DB Passwort

    // Verbindung zur Datenbank herstellen
    public Connection connect() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to PostgreSQL database!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database");
            e.printStackTrace();
        }
        return connection;
    }

    //Testen der Datenbank
    public void testConnection() {
        try (Connection connection = connect();
             PreparedStatement stmt = connection.prepareStatement("SELECT 1")) { // Eine einfache Abfrage
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Successfully connected to the database!");
            }
        } catch (SQLException e) {
            System.out.println("Error executing test query: " + e.getMessage());
        }
    }
}


