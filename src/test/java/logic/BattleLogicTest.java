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

        User player1 = new User(UUID.randomUUID(), "player1" , null, null, null, null, null, null);
        User player2 = new User(UUID.randomUUID(), "player2", null, null, null, null, null, null);

        battleLogic.addPlayer(player1);
        battleLogic.addPlayer(player2);

        // Überprüfe, ob beide Spieler hinzugefügt wurden
        assertEquals(2, battleLogic.getPlayers());
    }

    @Test
    void testRoundDamage() {
        BattleLogic battleLogic = new BattleLogic();

        User player1 = new User(UUID.randomUUID(), "player1", null, null, null, null, null, null);
        User player2 = new User(UUID.randomUUID(), "player2", null, null, null, null, null, null);

        Card card1 = new MonsterCard(UUID.randomUUID(),"Goblin", 11, "Monster", "Normal", null, null);
        Card card2 = new MonsterCard(UUID.randomUUID(),"Dragon", 70, "Monster", "Normal", null, null);

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

        Card wizzard = new MonsterCard(UUID.randomUUID(),"Wizzard", 11, "Monster", "Normal", null, null);
        Card ork = new MonsterCard(UUID.randomUUID(),"Ork", 9, "Monster", "Normal", null, null);

        int damageWizzardVsOrk = battleLogic.calculateDamage(wizzard, ork);

        // Der Schaden des Wizzards gegen den Ork sollte 0 sein
        assertEquals(0, damageWizzardVsOrk);
    }

    @Test
    void testKnightVsWaterSpell() {
        BattleLogic battleLogic = new BattleLogic();

        Card knight = new MonsterCard(UUID.randomUUID(),"Knight", 20, "Monster", "Normal", null, null);
        Card waterSpell = new SpellCard(UUID.randomUUID(),"WaterSpell", 22, "Spell", "Water", null, null);

        // Berechne den Schaden zwischen Knight und WaterSpell
        int damageKnightVsWaterSpell = battleLogic.calculateDamage(knight, waterSpell);
        int damageWaterSpellVsKnight = battleLogic.calculateDamage(waterSpell, knight);


        assertTrue(damageKnightVsWaterSpell == -100); // Knight verliert sofort

        assertTrue(damageWaterSpellVsKnight >= 0); // Der Schaden sollte nicht negativ sein
    }

    @Test
    void testKrakenVsSpells() {
        BattleLogic battleLogic = new BattleLogic();

        Card kraken = new MonsterCard(UUID.randomUUID(),"Kraken", 20, "Monster", "Normal", null, null);
        Card waterSpell = new SpellCard(UUID.randomUUID(),"WaterSpell", 19, "Spell", "Water", null, null);

        int damageKrakenVsWaterSpell = battleLogic.calculateDamage(kraken, waterSpell);
        int damageWaterSpellVsKraken = battleLogic.calculateDamage(waterSpell, kraken);

        //Schaden extrem hoch
        assertEquals(100, damageKrakenVsWaterSpell);

        //Keinen extremen Schaden gegen Kraken machen
        assertTrue(damageWaterSpellVsKraken >= 0);
    }

    @Test
    void testFireElfVsDragon() {
        BattleLogic battleLogic = new BattleLogic();

        Card fireElf = new MonsterCard(UUID.randomUUID(),"FireElf", 11, "Monster", "Fire", null, null);
        Card dragon = new MonsterCard(UUID.randomUUID(),"Dragon", 12, "Normal", "Normel", null, null);

        int damageFireElfVsDragon = battleLogic.calculateDamage(fireElf, dragon);
        int damageDragonVsFireElf = battleLogic.calculateDamage(dragon, fireElf);

        //Schaden soll 0 sein
        assertEquals(0, damageFireElfVsDragon);

        // Dragon sollte normalen Schaden gegen FireElf machen
        assertTrue(damageDragonVsFireElf > 0);
    }

    @Test
    void testWaterVsFire() {
        BattleLogic battleLogic = new BattleLogic();

        Card waterCard = new SpellCard(UUID.randomUUID(),"WaterCard", 20, "Spell","Water", null , null);
        Card fireCard = new SpellCard(UUID.randomUUID(),"FireCard", 40, "Spell","Fire", null, null  );

        int damageWaterVsFire = battleLogic.calculateDamage(waterCard, fireCard);
        int damageFireVsWater = battleLogic.calculateDamage(fireCard, waterCard);

        assertTrue(damageWaterVsFire > damageFireVsWater);
    }

    @Test
    void testNormalVsWater() {
        BattleLogic battleLogic = new BattleLogic();

        Card normalCard = new MonsterCard(UUID.randomUUID(),"NormalCard", 21, "Random", "Normal", null, null);
        Card waterCard = new MonsterCard(UUID.randomUUID(),"WaterCard", 25, "Random" ,"Water", null, null);

        int damageNormalVsWater = battleLogic.calculateDamage(normalCard, waterCard);
        int damageWaterVsNormal = battleLogic.calculateDamage(waterCard, normalCard);

        // Normal verursacht weniger Schaden gegen Water
        assertTrue(damageNormalVsWater < damageWaterVsNormal);
    }

}
