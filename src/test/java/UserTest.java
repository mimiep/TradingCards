package org.example.models;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserConstructorAndGetters() {
        // Setup
        UUID id = UUID.randomUUID();
        String username = "testUser";
        String password = "testPassword";
        String token = "testUser-mtcgToken";
        Integer coins = 20;

        // Benutzerobjekt erstellen
        User user = new User(id, username, password, token, coins);

        // Überprüfen, ob die Getter die richtigen Werte zurückgeben
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(token, user.getToken());
    }

    @Test
    public void testUserSetters() {
        // Setup
        User user = new User();

        // Setter verwenden
        UUID id = UUID.randomUUID();
        String username = "newUser";
        String password = "newPassword";
        String token = "newUserToken";

        user.setId(id);
        user.setUsername(username);
        user.setPassword(password);
        user.setToken(token);

        // Überprüfen, ob die Setter die richtigen Werte gesetzt haben
        assertEquals(id, user.getId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
        assertEquals(token, user.getToken());
    }
}
