package database;

import org.example.database.Database;
import org.junit.jupiter.api.*;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    private Database database;

    @BeforeEach
    void setUp() {
        database = new Database(); // Initialisiert die Datenbankklasse vor jedem Test
    }

    @Test
    void testConnect_Success() {
        Connection connection = database.connect();
        assertNotNull(connection, "Die Verbindung sollte nicht null sein, wenn sie erfolgreich ist.");
    }

    @Test
    void testConnect_Failure() {
        Database faultyDatabase = new Database() {
            @Override
            public Connection connect() {
                return null; //Simuliert Error
            }
        };

        Connection connection = faultyDatabase.connect();
        assertNull(connection, "Die Verbindung sollte null sein, wenn sie fehlschlägt.");
    }

    @Test
    void testTestConnection() {
        assertDoesNotThrow(() -> database.testConnection(),
                "Die Methode `testConnection` sollte keine Ausnahme werfen.");
    }
}
