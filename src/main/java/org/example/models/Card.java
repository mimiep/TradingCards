package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public abstract class Card {
    private UUID cardId;
    private UUID packageId;
    private UUID userId;

    @JsonProperty("Name") public String name;
    @JsonProperty("Damage") public int damage;
    @JsonProperty("Type") public String type; // Monster oder Spell
    @JsonProperty("ElementType") public String elementType;

    public Card() {}

    public Card(UUID cardId, UUID packageId, UUID userId, String name, int damage, String type, String elementType) {
        this.cardId = cardId;
        this.packageId = packageId;
        this.userId = userId;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.elementType = elementType;
    }

    // Getter und Setter
    public UUID getCardId() {
        return cardId;
    }

    public void setCardId(UUID cardId) {
        this.cardId = cardId;
    }

    public UUID getPackageId() {
        return packageId;
    }

    public void setPackageId(UUID packageId) {
        this.packageId = packageId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

}