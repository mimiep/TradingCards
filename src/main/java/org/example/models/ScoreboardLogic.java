package org.example.models;

import org.example.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreboardLogic {
    private Database database;


    public ScoreboardLogic() {
        this.database = new Database();
    }


    // Benutzerspezifische Statistiken abrufen
    public int getUserElo(UUID userId) throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "SELECT elo FROM scoreboard WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setObject(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("elo");
                } else {
                    throw new SQLException("User not found in scoreboard");
                }
            }
        }
    }

    // Gesamtes Scoreboard abrufen
    public List<ScoreboardEntry> getScoreboard() throws SQLException {
        try (Connection connection = database.connect()) {
            String query = "SELECT u.username, s.elo FROM scoreboard s JOIN users u ON s.user_id = u.id ORDER BY s.elo DESC";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                List<ScoreboardEntry> scoreboard = new ArrayList<>();
                while (rs.next()) {
                    String username = rs.getString("username");
                    int elo = rs.getInt("elo");
                    scoreboard.add(new ScoreboardEntry(username, elo));
                }
                return scoreboard;
            }
        }
    }

    public void insertUserScoreboard(UUID userId) throws SQLException {
        try (Connection connection = database.connect()) {
            // Benutzer in die Scoreboard-Tabelle einfügen
            String insertScoreboardQuery = "INSERT INTO scoreboard (user_id, elo) VALUES (?, ?)";
            try (PreparedStatement insertScoreboardStmt = connection.prepareStatement(insertScoreboardQuery)) {
                insertScoreboardStmt.setObject(1, userId);  // user_id
                insertScoreboardStmt.setInt(2, 100);  // Start-ELO-Wert
                insertScoreboardStmt.executeUpdate();
            }
        }
    }
}