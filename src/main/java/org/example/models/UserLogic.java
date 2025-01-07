package org.example.models;

import org.example.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserLogic {

    private final Database database;

    public UserLogic() {
        this.database = new Database(); // Verbindung zur Datenbank
    }

    // Benutzer registrieren
    public boolean registerUser(String username, String password) throws SQLException {
        String token;
        try (Connection connection = database.connect()) {

            // Neuen Benutzer in die Datenbank einfügen
            String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.executeUpdate();
                return true; // Registrierung erfolgreich
            }
        }

    }


    // Benutzer einloggen und Token generieren
    public String loginUser(String username, String password) throws SQLException {
        try (Connection connection = database.connect()) {
            // Überprüfen, ob Benutzername und Passwort korrekt sind
            String loginQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement loginStmt = connection.prepareStatement(loginQuery)) {
                loginStmt.setString(1, username);
                loginStmt.setString(2, password);
                ResultSet rs = loginStmt.executeQuery();

                if (rs.next()) {
                    // Token generieren
                    String token = generateToken(username);

                    // Den generierten Token in der Datenbank speichern
                    String updateTokenQuery = "UPDATE users SET token = ? WHERE username = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateTokenQuery)) {
                        updateStmt.setString(1, token);
                        updateStmt.setString(2, username);
                        updateStmt.executeUpdate();
                    }

                    return token; // Erfolgreiches Login, Token zurückgeben
                } else {
                    return null; // Login fehlgeschlagen
                }
            }
        }
    }

    // Hilfsmethode zum Generieren eines eindeutigen Tokens (z.B. UUID)
    public String generateToken(String username) {
        return  username + "-mtcgToken"; // Einfache Generierung eines eindeutigen Tokens
    }

    // Überprüfung, ob ein Token gültig ist
    public boolean validateToken(String token) throws SQLException {
        try (Connection connection = database.connect()) {
            // Überprüfen, ob der Token in der Datenbank existiert
            String validateQuery = "SELECT * FROM users WHERE token = ?";
            try (PreparedStatement validateStmt = connection.prepareStatement(validateQuery)) {
                validateStmt.setString(1, token);
                ResultSet rs = validateStmt.executeQuery();

                return rs.next(); // Token ist gültig, wenn eine Übereinstimmung gefunden wird
            }
        }
    }

    public User getUserByToken(String token) throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "SELECT * FROM users WHERE token = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, token);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new User(
                            UUID.fromString(rs.getString("id")),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("token"),
                            rs.getInt("coins"),
                            rs.getString("name"),
                            rs.getString("bio"),
                            rs.getString("image")
                    );
                }
            }
        }
        return null; // Benutzer nicht gefunden
    }

    public UUID getUserIdFromToken(String token) throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "SELECT id FROM users WHERE token = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, token);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return UUID.fromString(rs.getString("id"));
                }
            }
        }
        return null; // Benutzer nicht gefunden
    }

    public void deductCoins(UUID userId, int amount) throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "UPDATE users SET coins = coins - ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, amount);
                stmt.setObject(2, userId);
                stmt.executeUpdate();
            }
        }
    }

    public boolean updateUser(String username, String name, String bio, String image) throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "UPDATE users SET name = ?, bio = ?, image = ? WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, bio);
                stmt.setString(3, image);
                stmt.setString(4, username);
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                                UUID.fromString(rs.getString("id")),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("token"),
                                rs.getInt("coins"),
                                rs.getString("name"),
                                rs.getString("bio"),
                                rs.getString("image")
                        );
                    } else {
                        return null; // Benutzer nicht gefunden
                    }
                }
            }
        }
    }


    public String getUsernameFromId(UUID userId) {
        try (Connection connection = database.connect()) {
            String query = "SELECT username FROM users WHERE id = ?"; // UUID direkt vergleichen
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setObject(1, userId); // UUID als Parameter übergeben
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("username");
                    } else {
                        return null; // Benutzer nicht gefunden
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Fehlerbehandlung bei der DB-Abfrage
        }
    }

    public UUID getUserIdByUsername(String username) throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return UUID.fromString(rs.getString("id"));
                }
            }
        }
        return null; // Benutzer nicht gefunden
    }

    public int getElo(UUID userId) throws SQLException {
        int elo = 0;
        try (Connection connection = database.connect()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT elo FROM scoreboard WHERE user_id = ?");
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                elo = rs.getInt("elo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return elo;
    }

    public void setElo(int elo, UUID userId) {
        try (Connection connection = database.connect()) {
            PreparedStatement stmt = connection.prepareStatement("UPDATE scoreboard SET elo = ? WHERE user_id = ?");
            stmt.setInt(1, elo);
            stmt.setObject(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
