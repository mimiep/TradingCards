package org.example.models;

public abstract class Card {
    private String id;
    private String name;
    private double damage;

    public Card(String id, String name, double damage) {
        this.id = id;
        this.name = name;
        this.damage = damage;
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }
}