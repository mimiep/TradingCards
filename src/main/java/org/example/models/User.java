package org.example.models;

import java.util.UUID;

public class User {
    private String id;           // Benutzer-ID (UUID)
    private String username;     // Benutzername
    private String password;      // Passwort
    private int coins;           // Anzahl der verfügbaren Coins
    private int elo;             // ELO-Wertung

    // Konstruktor für die Benutzerklasse
    public User(String username, String password) {
        this.id = UUID.randomUUID().toString(); // UUID generieren
        this.username = username;
        this.password = password;
        this.coins = 20; // Standard: 20 Coins beim Erstellen eines neuen Benutzers
        this.elo = 100;  // Start-ELO-Wertung
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }
}
