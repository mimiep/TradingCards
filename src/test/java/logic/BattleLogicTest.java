package logic;

import org.example.models.*;
import org.example.logic.BattleLogic;
import org.junit.jupiter.api.Test;

import org.example.models.User;
import org.example.logic.BattleLogic;
import org.example.logic.DeckLogic;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


public class BattleLogicTest {

    @Test
    void testAddPlayers() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle zwei Benutzer
        User player1 = new User(UUID.randomUUID(), "player1" , null, null, null, null, null, null);
        User player2 = new User(UUID.randomUUID(), "player2", null, null, null, null, null, null);

        // Spieler hinzufügen
        battleLogic.addPlayer(player1);
        battleLogic.addPlayer(player2);

        // Überprüfe, ob beide Spieler hinzugefügt wurden
        assertEquals(2, battleLogic.getPlayers());
    }

    @Test
    void testRoundDamage() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle zwei Spieler
        User player1 = new User(UUID.randomUUID(), "player1", null, null, null, null, null, null);
        User player2 = new User(UUID.randomUUID(), "player2", null, null, null, null, null, null);

        // Erstelle Karten
        Card card1 = new MonsterCard(UUID.randomUUID(),"Goblin", 11, "Monster", "Normal", null, null);
        Card card2 = new MonsterCard(UUID.randomUUID(),"Dragon", 70, "Monster", "Normal", null, null);

        // Decks initialisieren (dies könnte durch die DeckLogic erfolgen)
        battleLogic.addPlayer(player1);
        battleLogic.addPlayer(player2);

        // Test: Prüfe die Schadensberechnung in der BattleLogic
        int damage1 = battleLogic.calculateDamage(card1, card2);
        int damage2 = battleLogic.calculateDamage(card2, card1);

        // Der Schaden für den Goblin sollte kleiner sein als für den Drachen
        assertTrue(damage1 < damage2);
    }

    @Test
    void testWizzardVsOrk() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle Karten
        Card wizzard = new MonsterCard(UUID.randomUUID(),"Wizzard", 11, "Monster", "Normal", null, null);
        Card ork = new MonsterCard(UUID.randomUUID(),"Ork", 9, "Monster", "Normal", null, null);

        // Berechne den Schaden zwischen Wizzard und Ork
        int damageWizzardVsOrk = battleLogic.calculateDamage(wizzard, ork);

        // Der Schaden des Wizzards gegen den Ork sollte 0 sein
        assertEquals(0, damageWizzardVsOrk);
    }

    @Test
    void testKnightVsWaterSpell() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle Karten
        Card knight = new MonsterCard(UUID.randomUUID(),"Knight", 20, "Monster", "Normal", null, null);
        Card waterSpell = new SpellCard(UUID.randomUUID(),"WaterSpell", 22, "Spell", "Water", null, null);

        // Berechne den Schaden zwischen Knight und WaterSpell
        int damageKnightVsWaterSpell = battleLogic.calculateDamage(knight, waterSpell);
        int damageWaterSpellVsKnight = battleLogic.calculateDamage(waterSpell, knight);

        // Der Knight verliert sofort gegen den WaterSpell (negativer Schaden)
        assertTrue(damageKnightVsWaterSpell == -100); // Knight verliert sofort

        // WaterSpell sollte nicht gegen den Knight gewinnen, aber auch keinen gewaltigen Schaden machen
        assertTrue(damageWaterSpellVsKnight >= 0); // Der Schaden sollte nicht negativ sein
    }

    @Test
    void testKrakenVsSpells() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle Karten
        Card kraken = new MonsterCard(UUID.randomUUID(),"Kraken", 20, "Monster", "Normal", null, null);
        Card waterSpell = new SpellCard(UUID.randomUUID(),"WaterSpell", 19, "Spell", "Water", null, null);

        // Berechne den Schaden zwischen Kraken und WaterSpell
        int damageKrakenVsWaterSpell = battleLogic.calculateDamage(kraken, waterSpell);
        int damageWaterSpellVsKraken = battleLogic.calculateDamage(waterSpell, kraken);

        // Kraken ist immun gegen Spells, daher sollte der Schaden extrem hoch sein
        assertEquals(100, damageKrakenVsWaterSpell);

        // WaterSpell sollte keinen extremen Schaden gegen Kraken machen
        assertTrue(damageWaterSpellVsKraken >= 0);
    }

    @Test
    void testFireElfVsDragon() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle Karten
        Card fireElf = new MonsterCard(UUID.randomUUID(),"FireElf", 11, "Monster", "Fire", null, null);
        Card dragon = new MonsterCard(UUID.randomUUID(),"Dragon", 12, "Normal", "Normel", null, null);

        // Berechne den Schaden zwischen FireElf und Dragon
        int damageFireElfVsDragon = battleLogic.calculateDamage(fireElf, dragon);
        int damageDragonVsFireElf = battleLogic.calculateDamage(dragon, fireElf);

        // FireElf weicht dem Dragon aus, daher sollte der Schaden 0 sein
        assertEquals(0, damageFireElfVsDragon);

        // Dragon sollte normalen Schaden gegen FireElf machen
        assertTrue(damageDragonVsFireElf > 0);
    }

    @Test
    void testWaterVsFire() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle Karten
        Card waterCard = new SpellCard(UUID.randomUUID(),"WaterCard", 20, "Spell","Water", null , null);
        Card fireCard = new SpellCard(UUID.randomUUID(),"FireCard", 40, "Spell","Fire", null, null  );

        // Berechne den Schaden zwischen WaterCard und FireCard
        int damageWaterVsFire = battleLogic.calculateDamage(waterCard, fireCard);
        int damageFireVsWater = battleLogic.calculateDamage(fireCard, waterCard);

        // Water verursacht doppelten Schaden gegen Fire
        assertTrue(damageWaterVsFire > damageFireVsWater);
    }

    @Test
    void testNormalVsWater() {
        BattleLogic battleLogic = new BattleLogic();

        // Erstelle Karten
        Card normalCard = new MonsterCard(UUID.randomUUID(),"NormalCard", 21, "Random", "Normal", null, null);
        Card waterCard = new MonsterCard(UUID.randomUUID(),"WaterCard", 25, "Random" ,"Water", null, null);

        // Berechne den Schaden zwischen NormalCard und WaterCard
        int damageNormalVsWater = battleLogic.calculateDamage(normalCard, waterCard);
        int damageWaterVsNormal = battleLogic.calculateDamage(waterCard, normalCard);

        // Normal verursacht weniger Schaden gegen Water
        assertTrue(damageNormalVsWater < damageWaterVsNormal);
    }

}
