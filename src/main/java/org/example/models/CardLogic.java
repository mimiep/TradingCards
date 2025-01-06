package org.example.models;

import org.example.database.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CardLogic {
    private final Database database;


    public CardLogic() {
        this.database = new Database();
    }

    public void createCard(UUID cardId, String name, int damage, String type, String elementType, UUID packageId, UUID userId) throws SQLException {

        userId=null;

        try (Connection connection = database.connect()) {
            String insertCardQuery = "INSERT INTO cards (card_id, name, damage, type, element_type, package_id, user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertCardQuery)) {
                stmt.setObject(1, cardId);
                stmt.setString(2, name);
                stmt.setInt(3, damage);  // damage als Integer
                stmt.setString(4, type);
                stmt.setString(5, elementType);
                stmt.setObject(6, packageId);
                stmt.setObject(7, userId);  // userId aus der Authentifizierung
                stmt.executeUpdate();
            }
        }
    }


    public List<Card> getCardsByUser(UUID userId) throws SQLException {
        List<Card> cards = new ArrayList<>();
        try (Connection connection = database.connect()) {
            String query = "SELECT * FROM cards WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setObject(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        UUID cardId = UUID.fromString(rs.getString("card_id"));
                        String name = rs.getString("name");
                        int damage = rs.getInt("damage");
                        String type = rs.getString("type");
                        String elementType = rs.getString("element_type");
                        UUID packageId = UUID.fromString(rs.getString("package_id"));
                        UUID user_id = UUID.fromString(rs.getString("user_id"));

                        // Dynamische Erstellung der Karte basierend auf dem Typ
                        Card card;
                        if ("Spell".equals(type)) {
                            card = new SpellCard(cardId, name, damage, type, elementType, packageId, user_id);
                        } else {
                            card = new MonsterCard(cardId, name, damage, type, elementType, packageId, user_id);
                        }
                        cards.add(card);
                    }
                }
            }
        }
        return cards;
    }



}