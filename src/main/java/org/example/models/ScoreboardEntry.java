package org.example.models;


public class ScoreboardEntry {
    private String username;
    private int elo;

    public ScoreboardEntry(String username, int elo) {
        this.username = username;
        this.elo = elo;
    }

    public String getUsername() {
        return username;
    }

    public int getElo() {
        return elo;
    }
}