package org.example.models;

import java.util.UUID;

public class MonsterCard extends Card {

        public MonsterCard() {}

        public MonsterCard(UUID cardId, UUID packageId, UUID userId, String name, int damage, String elementType) {
            super(cardId, packageId, userId, name, damage, "Monster", elementType);
        }
}
