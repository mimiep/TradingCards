package org.example.models;

import java.util.UUID;

public class SpellCard extends Card {
    public SpellCard() {}

    public SpellCard(UUID cardId, UUID packageId, UUID userId, String name, int damage, String elementType) {
        super(cardId, packageId, userId, name, damage, "Spell", elementType);
    }
    
}