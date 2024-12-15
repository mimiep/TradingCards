package org.example.models;

import java.util.UUID;

public class Deck {
        private UUID userId;
        private UUID cardId;

        public Deck() {}

        public Deck(UUID userId, UUID cardId) {
            this.userId = userId;
            this.cardId = cardId;
        }

        //Getter und Setter
        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public UUID getCardId() {
            return cardId;
        }

        public void setCardId(UUID cardId) {
            this.cardId = cardId;
        }
    
}