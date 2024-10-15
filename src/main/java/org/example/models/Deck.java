package org.example.models;

import java.util.List;

public class Deck {
    private List<Card> cards;  //List aus Cards

    public Deck(List<Card> cards) {
        this.cards = cards;
    }

    // Getter und Setter
    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }
    
}