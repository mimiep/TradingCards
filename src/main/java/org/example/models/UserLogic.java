package org.example.models;

import org.example.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserLogic {

    private final Database database;

    public UserLogic() {
        this.database = new Database(); // Verbindung zur Datenbank initialisieren
    }

    // Benutzer registrieren
    public boolean registerUser(String username, String password) throws SQLException {
        try (Connection connection = database.connect()) {
            // Überprüfen, ob der Benutzer bereits existiert
            String checkUserQuery = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkUserQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return false; // Benutzer existiert bereits
                }
            }

            // Neuen Benutzer einfügen
            String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.executeUpdate();
                return true; // Registrierung erfolgreich
            }
        }
    }

    // Benutzer einloggen
    public String loginUser(String username, String password) throws SQLException {
        try (Connection connection = database.connect()) {
            // Benutzername und Passwort überprüfen
            String loginQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement loginStmt = connection.prepareStatement(loginQuery)) {
                loginStmt.setString(1, username);
                loginStmt.setString(2, password);
                ResultSet rs = loginStmt.executeQuery();

                if (rs.next()) {
                    // Bei erfolgreichem Login einen Token generieren (in diesem Fall ein einfacher String)
                    return username + "-mtcgToken";
                } else {
                    return null; // Login fehlgeschlagen
                }
            }
        }
    }
}