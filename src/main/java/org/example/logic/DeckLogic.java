package org.example.logic;

import org.example.database.Database;
import org.example.models.Card;
import org.example.models.MonsterCard;
import org.example.models.SpellCard;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckLogic {
    private final Database database;

    public DeckLogic() {
        this.database = new Database();
    }

    //Fügt in Table Deck die KartenID und UserID
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

    //Gibt CardId von Deck eines User zurück
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

    //gibt alle Karten von Deck zurück
    public List<Card> getCardsFromDeck(UUID userId) throws SQLException {
        List<Card> cards = new ArrayList<>();
        // Zuerst die UUIDs der Karten des Benutzers bekommen
        List<UUID> cardIds = getDeck(userId); // Hier verwenden wir die getDeck-Methode, um die UUIDs zu holen

        try (Connection connection = database.connect()) {
            // Jetzt holen wir die Kartendetails aus der Datenbank
            for (UUID cardId : cardIds) {
                String query = "SELECT * FROM cards WHERE card_id = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setObject(1, cardId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString("name");
                            int damage = rs.getInt("damage");
                            String type = rs.getString("type");
                            String elementType = rs.getString("element_type");
                            UUID user_id = UUID.fromString(rs.getString("user_id"));

                            // Dynamische Erstellung der Karte basierend auf dem Typ
                            Card card;
                            if ("Spell".equals(type)) {
                                card = new SpellCard(cardId, name, damage, type, elementType, null , user_id);
                            } else {
                                card = new MonsterCard(cardId, name, damage, type, elementType, null , user_id);
                            }
                            cards.add(card);
                        }
                    }
                }
            }
        }
        return cards;
    }


}
