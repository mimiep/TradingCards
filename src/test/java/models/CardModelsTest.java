package models;

import org.example.models.Card;
import org.example.models.MonsterCard;
import org.example.models.SpellCard;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CardModelsTest {
    @Test
    void testCardCreation() {
        UUID cardId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        Card card = new SpellCard(cardId, "Fireball", 50, "Spell", "Fire", packageId, userId);

        // Teste, ob die ID korrekt gesetzt wurde
        assertNotNull(card.getCardId());
        assertEquals(cardId, card.getCardId(), "Card ID should match");

        // Teste den Namen der Karte
        assertEquals("Fireball", card.getName(), "Card name should be 'Fireball'");

        // Teste den Schaden der Karte
        assertEquals(50, card.getDamage(), "Card damage should be 50");

        // Teste den Kartentyp
        assertEquals("Spell", card.getType(), "Card type should be 'Spell'");

        // Teste den Elementtyp
        assertEquals("Fire", card.getElementType(), "Element type should be 'Fire'");

        // Teste die PackageId
        assertEquals(packageId, card.getPackageId(), "Package ID should match");

        // Teste die UserId
        assertEquals(userId, card.getUserId(), "User ID should match");
    }

    @Test
    void testSpellCardInheritance() {
        UUID cardId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SpellCard spellCard = new SpellCard(cardId, "Water Blast", 40, "Spell", "Water", packageId, userId);

        // Teste, ob die SpellCard die Card-Eigenschaften korrekt erbt
        assertNotNull(spellCard.getCardId(), "Card ID should not be null");
        assertEquals("Water Blast", spellCard.getName(), "SpellCard name should be 'Water Blast'");
        assertEquals(40, spellCard.getDamage(), "SpellCard damage should be 40");
        assertEquals("Spell", spellCard.getType(), "SpellCard type should be 'Spell'");
        assertEquals("Water", spellCard.getElementType(), "SpellCard element type should be 'Water'");
        assertEquals(packageId, spellCard.getPackageId(), "Package ID should match");
        assertEquals(userId, spellCard.getUserId(), "User ID should match");
    }

    @Test
    void testDifferentCardInstances() {
        UUID cardId1 = UUID.randomUUID();
        UUID cardId2 = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SpellCard spellCard1 = new SpellCard(cardId1, "Fireball", 50, "Spell", "Fire", packageId, userId);
        SpellCard spellCard2 = new SpellCard(cardId2, "Ice Blast", 30, "Spell", "Water", packageId, userId);

        // Teste, dass beide Karten unterschiedliche IDs haben
        assertNotEquals(spellCard1.getCardId(), spellCard2.getCardId(), "Card IDs should be different");

        // Teste die Unterschiede der Karten
        assertEquals("Fireball", spellCard1.getName(), "First spell card name should be 'Fireball'");
        assertEquals("Ice Blast", spellCard2.getName(), "Second spell card name should be 'Ice Blast'");

        assertEquals(50, spellCard1.getDamage(), "First spell card damage should be 50");
        assertEquals(30, spellCard2.getDamage(), "Second spell card damage should be 30");

        assertEquals("Fire", spellCard1.getElementType(), "First spell card element type should be 'Fire'");
        assertEquals("Water", spellCard2.getElementType(), "Second spell card element type should be 'Water'");
    }

    @Test
    void testCardTypeConsistency() {
        UUID cardId = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SpellCard spellCard = new SpellCard(cardId, "Fireball", 50, "Spell", "Fire", packageId, userId);
        MonsterCard monsterCard = new MonsterCard(cardId, "Dragon", 80, "Monster", "Fire", packageId, userId);

        // Überprüfe die Kartentypen
        assertEquals("Spell", spellCard.getType(), "SpellCard should have type 'Spell'");
        assertEquals("Monster", monsterCard.getType(), "MonsterCard should have type 'Monster'");
    }

    @Test
    void testDifferentCardTypes() {
        UUID cardId1 = UUID.randomUUID();
        UUID cardId2 = UUID.randomUUID();
        UUID packageId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        SpellCard spellCard = new SpellCard(cardId1, "Fireball", 50, "Spell", "Fire", packageId, userId);
        MonsterCard monsterCard = new MonsterCard(cardId2, "Dragon", 80, "Monster", "Fire", packageId, userId);

        // Überprüfe, dass die IDs der Karten unterschiedlich sind
        assertNotEquals(spellCard.getCardId(), monsterCard.getCardId(), "Card IDs should be different");

        // Überprüfe, dass die Kartentypen unterschiedlich sind
        assertNotEquals(spellCard.getType(), monsterCard.getType(), "Card types should be different");
        assertEquals("Spell", spellCard.getType(), "First card should be 'Spell'");
        assertEquals("Monster", monsterCard.getType(), "Second card should be 'Monster'");
    }
}
