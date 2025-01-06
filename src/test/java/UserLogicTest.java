package org.example.models;

import org.example.database.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.*;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserLogicTest {

    private UserLogic userLogic;
    private Database mockDatabase;

    @BeforeEach
    public void setUp() {
        // Mocking der Datenbankverbindung
        mockDatabase = mock(Database.class);
        userLogic = new UserLogic(mockDatabase);
    }

    @Test
    public void testRegisterUser() throws SQLException {
        // Setup
        String username = "testUser";
        String password = "testPassword";

        // Stubben der Verbindung
        try (Connection connection = mock(Connection.class)) {
            PreparedStatement stmt = mock(PreparedStatement.class);
            when(mockDatabase.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(stmt);

            // Die Methode ausführen
            boolean result = userLogic.registerUser(username, password);

            // Überprüfen, ob die Methode true zurückgibt
            assertTrue(result);

            // Verifizieren, dass die richtigen Parameter an die SQL-Abfrage übergeben wurden
            verify(stmt).setString(1, username);
            verify(stmt).setString(2, password);
            verify(stmt).executeUpdate();
        }
    }

    @Test
    public void testLoginUser_Successful() throws SQLException {
        // Setup
        String username = "testUser";
        String password = "testPassword";

        // Mock der ResultSet und Datenbankverbindung
        try (Connection connection = mock(Connection.class)) {
            PreparedStatement loginStmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);
            when(mockDatabase.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(loginStmt);
            when(loginStmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true);

            // Die Methode ausführen
            String token = userLogic.loginUser(username, password);

            // Überprüfen, ob ein Token zurückgegeben wird
            assertNotNull(token);
            assertTrue(token.startsWith(username + "-mtcgToken")); // Token-Prüfung

            // Verifizieren, dass das Update-Statement für das Token ausgeführt wurde
            verify(loginStmt).setString(1, username);
            verify(loginStmt).setString(2, password);
        }
    }

    @Test
    public void testLoginUser_Failed() throws SQLException {
        // Setup
        String username = "nonExistingUser";
        String password = "wrongPassword";

        // Mock der ResultSet und Datenbankverbindung
        try (Connection connection = mock(Connection.class)) {
            PreparedStatement loginStmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);
            when(mockDatabase.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(loginStmt);
            when(loginStmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            // Die Methode ausführen
            String token = userLogic.loginUser(username, password);

            // Überprüfen, dass bei falschen Anmeldedaten kein Token zurückgegeben wird
            assertNull(token);
        }
    }

    @Test
    public void testValidateToken_Valid() throws SQLException {
        // Setup
        String validToken = "testUser-mtcgToken";

        // Mock der ResultSet und Datenbankverbindung
        try (Connection connection = mock(Connection.class)) {
            PreparedStatement validateStmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);
            when(mockDatabase.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(validateStmt);
            when(validateStmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(true); // Token ist gültig

            // Die Methode ausführen
            boolean isValid = userLogic.validateToken(validToken);

            // Überprüfen, dass der Token als gültig erkannt wird
            assertTrue(isValid);
        }
    }

    @Test
    public void testValidateToken_Invalid() throws SQLException {
        // Setup
        String invalidToken = "invalidToken";

        // Mock der ResultSet und Datenbankverbindung
        try (Connection connection = mock(Connection.class)) {
            PreparedStatement validateStmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);
            when(mockDatabase.connect()).thenReturn(connection);
            when(connection.prepareStatement(anyString())).thenReturn(validateStmt);
            when(validateStmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false); // Token ist ungültig

            // Die Methode ausführen
            boolean isValid = userLogic.validateToken(invalidToken);

            // Überprüfen, dass der Token als ungültig erkannt wird
            assertFalse(isValid);
        }
    }
}
