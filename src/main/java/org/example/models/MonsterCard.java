package org.example.models;

import java.util.UUID;

//Kind von Card
public class MonsterCard extends Card {

    public MonsterCard(UUID cardId, String name, int damage, String type, String elementType, UUID packageId, UUID userId) {
        super(cardId, name, damage, type, elementType, packageId, userId);
    }
}
