package org.example.models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MonsterCardLogic extends CardLogic {

    public void createMonsterCard(UUID cardId, String name, int damage, String elementType, UUID packageId, UUID userId) throws SQLException {
        createCard(cardId, name, damage, "Monster", elementType, packageId, userId); // Aufruf der allgemeinen Card-Erstellungslogik
    }

}
