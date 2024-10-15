package org.example.models;

public class SpellCard extends Card {
    private String elementType; //Feuer, Wasser, usw.

    public SpellCard(String id, String name, double damage, String elementType) {
        super(id, name, damage);
        this.elementType = elementType;
    }

    // Getter und Setter
    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }
    
}