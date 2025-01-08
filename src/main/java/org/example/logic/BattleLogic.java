package org.example.logic;
import org.example.models.Card;
import org.example.models.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BattleLogic {
    private final List<User> players;
    private final DeckLogic deckLogic;
    private final ScoreboardLogic scoreboardLogic;

    public BattleLogic() {
        this.players = new ArrayList<>();
        this.deckLogic = new DeckLogic();
        this.scoreboardLogic = new ScoreboardLogic();
    }

    public int getPlayers() {
        return players.size();
    }

    public void addPlayer(User user) {
        this.players.add(user);
    }

    public void startBattle() throws SQLException {
        System.out.println("********BATTLE STARTED***********");

        User player1 = players.get(0);
        User player2 = players.get(1);

        UUID userid1 = player1.getUserId();
        UUID userid2 = player2.getUserId();

        List<Card> deck1 = deckLogic.getCardsFromDeck(userid1);
        List<Card> deck2 = deckLogic.getCardsFromDeck(userid2);

        if (deck1.isEmpty() || deck2.isEmpty()) {
            System.out.println("One or both players have no cards to battle.");
            return;
        }

        Random random = new Random();
        StringBuilder battleLog = new StringBuilder("Battle Log:\n");
        int rounds = 0;

        while (!deck1.isEmpty() && !deck2.isEmpty() && rounds < 13) { //da gehört eigentlih 100 hin, aber das ist hässlich zu lesen
            rounds++;
            //battleLog.append("Round ").append(rounds).append(":\n");
            battleLog.append(
                    " _______________\r\n" +
                            "|   ROUND " + rounds + "    |\r\n" +  // Rundennummer hier einfügen
                            "|_______________|\r\n"
            );

            Card card1 = deck1.get(random.nextInt(deck1.size()));
            Card card2 = deck2.get(random.nextInt(deck2.size()));

            battleLog.append(player1.getUsername()).append("'s card: ").append(card1.getName())
                    .append(" (Damage: ").append(card1.getDamage()).append(", Type: ").append(card1.getType()).append(")\n");
            battleLog.append(player2.getUsername()).append("'s card: ").append(card2.getName())
                    .append(" (Damage: ").append(card2.getDamage()).append(", Type: ").append(card2.getType()).append(")\n");

            int damage1 = calculateDamage(card1, card2);
            int damage2 = calculateDamage(card2, card1);

            if (damage1 > damage2) {
                battleLog.append(player1.getUsername()).append("'s card wins!\n");
                battleLog.append(
                        "+---------+\n" +
                                "|         |\n" +
                                "|   ♠ ♣   |\n" +
                                "|  CHANGE |\n" +
                                "|   ♥ ♦   |\n" +
                                "|         |\n" +
                                "+---------+\n"
                );
                deck2.remove(card2);
                deck1.add(card2);
            } else if (damage1 < damage2) {
                battleLog.append(player2.getUsername()).append("'s card wins!\n");
                battleLog.append(
                        "+---------+\n" +
                                "|         |\n" +
                                "|   ♠ ♣   |\n" +
                                "|  CHANGE |\n" +
                                "|   ♥ ♦   |\n" +
                                "|         |\n" +
                                "+---------+\n"
                );
                deck1.remove(card1);
                deck2.add(card1);
            } else {
                battleLog.append("It's a draw! No cards are exchanged.\n");
                battleLog.append(
                        "+---------+\n" +
                                "|         |\n" +
                                "|   ♠ ♣   |\n" +
                                "|   DRAW  |\n" +
                                "|   ♥ ♦   |\n" +
                                "|         |\n" +
                                "+---------+\n"
                );
            }

            battleLog.append("\n");
        }

        //der mit den meisten Karten am Ende gewinnst bzw. der der keine Karten mehr hat verliert
        String winner;
        if (deck2.size()> deck1.size()) {
            winner = player2.getUsername();
            scoreboardLogic.updateElo(player2, player1, true);
        } else if (deck1.size()> deck2.size()) {
            winner = player1.getUsername();
            scoreboardLogic.updateElo(player1, player2, true);
        } else if (deck2.isEmpty()) {
            winner = player1.getUsername();
            scoreboardLogic.updateElo(player1, player2, true);
        } else if (deck1.isEmpty()) {
            winner = player2.getUsername();
            scoreboardLogic.updateElo(player2, player1, true);
        } else {
            winner = "No one (draw)";
            scoreboardLogic.updateElo(player1, player2, false);
        }

        battleLog.append("Battle finished after ").append(rounds).append(" rounds. Winner: ").append(winner).append("\n");
        battleLog.append(
                "      _____\r\n" +
                        "     |     |\r\n" +
                        "     |  🏆 |\r\n" +
                        "     |_____| \r\n"
        );
        System.out.println(battleLog.toString());

        battleLog.setLength(0); //entleeren

    }

    public int calculateDamage(Card attacker, Card defender) {
        // Goblins vs Dragons

        if (attacker.getName().equalsIgnoreCase("Goblin") && defender.getName().equalsIgnoreCase("Dragon")) {
            return 0; // Goblins greifen keine Drachen an
        }
        // Wizzard vs Orks
        else if (attacker.getName().equalsIgnoreCase("Wizzard") && defender.getName().equalsIgnoreCase("Ork")) {
            return 0; // Orks greifen keine Wizzards an
        }
        // Knights vs WaterSpell
        else if (attacker.getName().equalsIgnoreCase("Knight") && defender.getName().equalsIgnoreCase("WaterSpell")) {
            return (-100); // Knights verlieren sofort gegen WaterSpell
        }
        // Kraken vs Spells
        else if (attacker.getName().equalsIgnoreCase("Kraken") && defender.getType().contains("Spell")) {
            return 100; // Kraken sind immun gegen Spells
        }
        // FireElves vs Dragons
        else if (attacker.getName().equalsIgnoreCase("FireElf") && defender.getName().equalsIgnoreCase("Dragon")) {
            return 0; // FireElves weichen Drachen aus
        }


        // Berechnung der Effektivität
        double multiplier = 1.0;

        if (attacker.getType().contains("Spell") || defender.getType().contains("Spell")) {
            String attackerElement = attacker.getElementType();
            String defenderElement = defender.getElementType();

            if (attackerElement.equalsIgnoreCase("Water") && defenderElement.equalsIgnoreCase("Fire")) {
                multiplier = 2.0; // Water -> Fire
            } else if (attackerElement.equalsIgnoreCase("Fire") && defenderElement.equalsIgnoreCase("Normal")) {
                multiplier = 2.0; // Fire -> Normal
            } else if (attackerElement.equalsIgnoreCase("Normal") && defenderElement.equalsIgnoreCase("Water")) {
                multiplier = 2.0; // Normal -> Water
            } else if (attackerElement.equalsIgnoreCase("Fire") && defenderElement.equalsIgnoreCase("Water")) {
                multiplier = 0.5; // Fire <- Water
            } else if (attackerElement.equalsIgnoreCase("Normal") && defenderElement.equalsIgnoreCase("Fire")) {
                multiplier = 0.5; // Normal <- Fire
            } else if (attackerElement.equalsIgnoreCase("Water") && defenderElement.equalsIgnoreCase("Normal")) {
                multiplier = 0.5; // Water <- Normal
            }
        }
        return (int) (attacker.getDamage() * multiplier);
    }

}
