package org.example.models;

import java.util.UUID;


public class MonsterCard extends Card {

    public MonsterCard(UUID cardId, String name, int damage, String type, String elementType, UUID packageId, UUID userId) {
        super(cardId, name, damage, type, elementType, packageId, userId);
    }
}
