package org.example.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Database {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/mtcg";
    private static final String DB_USER = "postgres"; // Dein DB Benutzername
    private static final String DB_PASSWORD = "your_password"; // Dein DB Passwort

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

    // Beispiel: User in der Datenbank speichern
    public void insertUser(UUID id, String username, String password) {
        String sql = "INSERT INTO users (id, username, password) VALUES (?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setObject(1, id);
            pstmt.setString(2, username);
            pstmt.setString(3, password);

            pstmt.executeUpdate();
            System.out.println("User added to the database");

        } catch (SQLException e) {
            System.out.println("Error inserting user");
            e.printStackTrace();
        }
    }

    // Beispiel: Abfrage eines Users aus der Datenbank
    public void getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("User found: " + rs.getString("username"));
                // Weitere Informationen des Users abrufen und ausgeben
                System.out.println("User ID: " + rs.getObject("id"));
                System.out.println("ELO: " + rs.getInt("elo"));
            } else {
                System.out.println("User not found.");
            }

        } catch (SQLException e) {
            System.out.println("Error fetching user");
            e.printStackTrace();
        }
    }

    // Beispiel: Coins für einen User aktualisieren
    public void updateUserCoins(UUID id, int newCoins) {
        String sql = "UPDATE users SET coins = ? WHERE id = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, newCoins);
            pstmt.setObject(2, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User coins updated successfully");
            } else {
                System.out.println("User not found");
            }

        } catch (SQLException e) {
            System.out.println("Error updating user coins");
            e.printStackTrace();
        }
    }
}

