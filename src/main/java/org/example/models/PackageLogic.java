package org.example.models;

import org.example.database.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.sql.ResultSet;


public class PackageLogic {
    private final Database database;
    private final CardLogic cardLogic;

    public PackageLogic() {
        this.database = new Database();
        this.cardLogic = new CardLogic();
    }

    public UUID createPackage() throws SQLException {
        UUID packageId = UUID.randomUUID();
        try (Connection connection = database.connect()) {
            String insertPackageQuery = "INSERT INTO packages (package_id) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertPackageQuery)) {
                stmt.setObject(1, packageId);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to insert package");
                }
            }
        }
        return packageId;
    }

    public UUID createPackageList(List<Card> cards) throws SQLException {

        UUID packageId = UUID.randomUUID();

        try (Connection connection = database.connect()) {

            // Paket erstellen
            String insertPackageQuery = "INSERT INTO packages (package_id) VALUES (?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertPackageQuery)) {
                stmt.setObject(1, packageId);
                stmt.executeUpdate();
            }

            try {
                // Karten erstellen (CardLogic nutzen)
                for (Card card : cards) {
                    cardLogic.createCard(
                            card.getCardId(),
                            card.getName(),
                            card.getDamage(),
                            card.getType(),
                            card.getElementType(),
                            packageId,
                            card.getUserId() //derzeit Null bis Fehler gefunden
                    );
                }
            } catch (SQLException e) {
                throw e;
            }
        }
        return packageId;
    }
    public boolean acquirePackage(UUID userId) throws SQLException {
        try (Connection connection = database.connect()) {
            connection.setAutoCommit(false); // Transaktion starten

            // Überprüfen, ob der Benutzer genug Coins hat (5 Coins für 1 Paket)
            String selectUserCoinsQuery = "SELECT coins FROM users WHERE id = ?";
            int userCoins = 0;
            try (PreparedStatement stmt = connection.prepareStatement(selectUserCoinsQuery)) {
                stmt.setObject(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    userCoins = rs.getInt("coins");
                }
            }

            // Wenn der Benutzer nicht genug Coins hat, rollback durchführen und false zurückgeben
            if (userCoins < 5) {
                connection.rollback();
                return false;
            }

            // Ein Paket finden, das 5 Karten enthält, die noch keinem Benutzer zugeordnet sind
            String selectPackageQuery = "SELECT package_id FROM cards WHERE user_id IS NULL GROUP BY package_id HAVING COUNT(*) = 5 LIMIT 1";
            UUID packageId = null;

            try (PreparedStatement stmt = connection.prepareStatement(selectPackageQuery)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    packageId = UUID.fromString(rs.getString("package_id"));
                }
            }

            if (packageId == null) { // Kein verfügbares Paket gefunden
                connection.rollback();
                return false;
            }

            // Karten des Pakets dem Benutzer zuweisen
            String updateCardsQuery = "UPDATE cards SET user_id = ? WHERE package_id = ? AND user_id IS NULL";
            try (PreparedStatement stmt = connection.prepareStatement(updateCardsQuery)) {
                stmt.setObject(1, userId);
                stmt.setObject(2, packageId);
                stmt.executeUpdate();
            }

            // Transaktion abschließen
            connection.commit();
            return true;

        } catch (SQLException e) {
            // Fehlerbehandlung, rollback durchführen, falls etwas schiefgeht
            throw new SQLException("Fehler beim Erwerb des Pakets: " + e.getMessage(), e);
        }
    }


}
