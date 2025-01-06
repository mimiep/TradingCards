package org.example.models;

import org.example.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckLogic {
    private final Database database;

    public DeckLogic() {
        this.database = new Database();
    }

    public void addCardToDeck(UUID userId, UUID cardId) throws SQLException {
        try (Connection connection = database.connect()) {
            String insertDeckQuery = "INSERT INTO decks (user_id, card_id) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertDeckQuery)) {
                stmt.setObject(1, userId);
                stmt.setObject(2, cardId);
                stmt.executeUpdate();
            }
        }
    }

    public List<UUID> getDeck(UUID userId) throws SQLException {
        List<UUID> deck = new ArrayList<>();
        try (Connection connection = database.connect()) {
            String query = "SELECT card_id FROM decks WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setObject(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        deck.add(UUID.fromString(rs.getString("card_id")));
                    }
                }
            }
        }
        return deck;
    }


}
