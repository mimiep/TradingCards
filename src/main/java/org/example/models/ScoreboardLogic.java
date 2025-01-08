package org.example.models;

import org.example.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScoreboardLogic {
    private Database database;
    private final UserLogic userLogic;

    public ScoreboardLogic() {
        this.database = new Database();
        this.userLogic = new UserLogic();
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

    public void updateElo(User winner, User loser, boolean hasWinner) throws SQLException {
        UUID userid1 = winner.getUserId();
        UUID userid2 = loser.getUserId();

        int winnerElo = userLogic.getElo(userid1);
        int loserElo = userLogic.getElo(userid2);

        if (hasWinner) {
            winnerElo += 10;
            loserElo -= 10;

            System.out.println("ELO WIRD AUSGEBESSTER");
            userLogic.setElo(winnerElo,userid1);
            userLogic.setElo(loserElo,userid2);
        }
    }
}