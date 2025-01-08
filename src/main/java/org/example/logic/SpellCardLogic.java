package org.example.logic;

import java.sql.SQLException;
import java.util.UUID;

//Braucht eigendlich keiner, da es eh alles von CardLogic hat
public class SpellCardLogic extends CardLogic {

    public void createSpellCard(UUID cardId, String name, int damage, String elementType, UUID packageId, UUID userId) throws SQLException {
        createCard(cardId, name, damage, "Spell", elementType, packageId, userId); // Aufruf der allgemeinen Card-Erstellungslogik
    }

}
