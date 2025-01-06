package org.example.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.UUID;

//abstract ?? Problem
public abstract class Card {

    private UUID cardId;
    @JsonProperty("Name") private String name;
    @JsonProperty("Damage") private int damage;
    @JsonProperty("Type") private String type;
    @JsonProperty("ElementType") private String elementType;
    @JsonProperty("PackageId") private UUID packageId;
    private UUID userId;


    public Card(UUID cardId, String name, int damage, String type, String elementType, UUID packageId, UUID userId) {
        this.cardId = cardId;
        this.name = name;
        this.damage = damage;
        this.type = type;
        this.elementType = elementType;
        this.packageId = packageId;
        this.userId = userId;
    }

    // Getter und Setter
    public UUID getCardId() {
        return cardId;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public String getType() {
        return type;
    }

    public String getElementType() {
        return elementType;
    }

    public UUID getPackageId() {
        return packageId;
    }

    public UUID getUserId() {
        return userId;
    }

}