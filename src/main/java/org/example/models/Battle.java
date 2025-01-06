package org.example.models;

import java.util.UUID;

public class Battle {
    private UUID battleId;
    private UUID player1Id;
    private UUID player2Id;
    private UUID winnerId;

    public Battle() {}

    //Konstruktor
    public Battle(UUID battleId, UUID player1Id, UUID player2Id, UUID winnerId) {
        this.battleId = battleId;
        this.player1Id = player1Id;
        this.player2Id = player2Id;
        this.winnerId = winnerId;
    }

    // Getter und Setter
    public UUID getBattleId() {
        return battleId;
    }

    public void setBattleId(UUID battleId) {
        this.battleId = battleId;
    }

    public UUID getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(UUID player1Id) {
        this.player1Id = player1Id;
    }

    public UUID getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(UUID player2Id) {
        this.player2Id = player2Id;
    }

    public UUID getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(UUID winnerId) {
        this.winnerId = winnerId;
    }
}
