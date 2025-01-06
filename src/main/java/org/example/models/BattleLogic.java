package org.example.models;

import org.example.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

//alles nur Ideen, absolut keine AHnung ob das stimmen könnte

public class BattleLogic {
    /*
    private final Database database;

    public BattleLogic() {
        this.database = new Database();
    }

    public UUID createBattle(UUID player1Id, UUID player2Id) throws SQLException {
        UUID battleId = UUID.randomUUID();
        try (Connection connection = database.connect()) {
            String insertBattleQuery = "INSERT INTO battles (battle_id, player1_id, player2_id, battle_date) " +
                    "VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertBattleQuery)) {
                stmt.setObject(1, battleId);
                stmt.setObject(2, player1Id);
                stmt.setObject(3, player2Id);
                stmt.executeUpdate();
            }
        }
        return battleId;
    }

    public void setBattleWinner(UUID battleId, UUID winnerId) throws SQLException {
        try (Connection connection = database.connect()) {
            String updateWinnerQuery = "UPDATE battles SET winner_id = ? WHERE battle_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateWinnerQuery)) {
                stmt.setObject(1, winnerId);
                stmt.setObject(2, battleId);
                stmt.executeUpdate();
            }
        }
    }

*/
}
