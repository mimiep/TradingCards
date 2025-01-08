package org.example.logic;

import org.example.database.Database;
import org.example.models.Card;

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

    public void deletePackageById(UUID packageId) throws SQLException {
        try (Connection connection = database.connect()) { // Verbindung herstellen
            String updatePackageSQL = "UPDATE cards SET package_id = NULL WHERE package_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updatePackageSQL)) {
                stmt.setObject(1, packageId, java.sql.Types.OTHER); // PostgreSQL erwartet java.sql.Types.OTHER für UUID
                int rowsAffected = stmt.executeUpdate();
            }
            System.out.println("GEUPDATED CARDS");
            String deletePackageSQL = "DELETE FROM packages WHERE package_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deletePackageSQL)) {
                stmt.setObject(1, packageId, java.sql.Types.OTHER); // PostgreSQL erwartet java.sql.Types.OTHER für UUID
                int rowsAffected = stmt.executeUpdate();
            }
            System.out.println("DELETED AUS PACKAGES");
        } catch (SQLException e) {
            throw new SQLException("Error while deleting package with ID: " + packageId, e);
        }

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
                System.out.println("GENUG COINS");
                return false;
            }

            // Ein Paket finden, das 5 Karten enthält, die noch keinem Benutzer zugeordnet sind
            String selectPackageQuery = "SELECT package_id FROM packages LIMIT 1";
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
            System.out.println("CARDS USER ZUWEISEN");

            String updateCardsQuery = "UPDATE cards SET user_id = ? WHERE package_id = ? AND user_id IS NULL";
            try (PreparedStatement stmt = connection.prepareStatement(updateCardsQuery)) {
                stmt.setObject(1, userId);
                stmt.setObject(2, packageId);
                stmt.executeUpdate();
            }


            // Transaktion abschließen
            connection.commit();
            deletePackageById(packageId);
            return true;

        } catch (SQLException e) {
            // Fehlerbehandlung, rollback durchführen, falls etwas schiefgeht
            throw new SQLException("Fehler beim Erwerb des Pakets: " + e.getMessage(), e);
        }
    }


}
