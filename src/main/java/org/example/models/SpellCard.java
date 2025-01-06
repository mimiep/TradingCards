package org.example.models;

import java.util.UUID;


public class SpellCard extends Card {


    public SpellCard(UUID cardId, String name, int damage, String type, String elementType, UUID packageId, UUID userId) {
        super(cardId, name, damage, type, elementType, packageId, userId);
    }
    
}