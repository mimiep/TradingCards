package models;

import org.example.models.User;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserModelsTest {

    @Test
    void testUserConstructorAndGetters() {
        // Erstelle ein User-Objekt
        UUID userId = UUID.randomUUID();
        String username = "TestUser";
        String password = "password123";
        String token = "testToken";
        Integer coins = 100;
        String name = "John Doe";
        String bio = "This is a bio.";
        String image = "image_url";

        User user = new User(userId, username, password, token, coins, name, bio, image);

        // Überprüfe, ob die Eigenschaften korrekt gesetzt wurden
        assertEquals(userId, user.getUserId(), "User ID should match");
        assertEquals(username, user.getUsername(), "Username should match");
        assertEquals(password, user.getPassword(), "Password should match");
        assertEquals(token, user.getToken(), "Token should match");
        assertEquals(coins, user.getCoins(), "Coins should match");
        assertEquals(name, user.getName(), "Name should match");
        assertEquals(bio, user.getBio(), "Bio should match");
        assertEquals(image, user.getImage(), "Image should match");
    }

    // Test für das Setzen und Abrufen von Eigenschaften
    @Test
    void testSetters() {
        User user = new User();

        // Setze die Eigenschaften mit den Settern
        UUID userId = UUID.randomUUID();
        user.setUsername("NewUser");
        user.setPassword("newpassword123");
        user.setToken("newToken");
        user.setCoins(50);
        user.setName("Jane Doe");
        user.setBio("This is another bio.");
        user.setImage("new_image_url");

        // Überprüfe, ob die Setzten-Werte mit den Gettern übereinstimmen
        assertEquals("NewUser", user.getUsername(), "Username should be 'NewUser'");
        assertEquals("newpassword123", user.getPassword(), "Password should be 'newpassword123'");
        assertEquals("newToken", user.getToken(), "Token should be 'newToken'");
        assertEquals(50, user.getCoins(), "Coins should be 50");
        assertEquals("Jane Doe", user.getName(), "Name should be 'Jane Doe'");
        assertEquals("This is another bio.", user.getBio(), "Bio should match");
        assertEquals("new_image_url", user.getImage(), "Image should be 'new_image_url'");
    }

    // Test für leere Benutzer-Instanz (keine Setzungen)
    @Test
    void testEmptyUser() {
        User user = new User();

        // Überprüfe, dass die Werte null sind, da nichts gesetzt wurde
        assertNull(user.getUsername(), "Username should be null");
        assertNull(user.getPassword(), "Password should be null");
        assertNull(user.getToken(), "Token should be null");
        assertNull(user.getCoins(), "Coins should be null");
        assertNull(user.getName(), "Name should be null");
        assertNull(user.getBio(), "Bio should be null");
        assertNull(user.getImage(), "Image should be null");
    }

    // Test für das Setzen von Coins und sicherstellen, dass der Wert geändert wird
    @Test
    void testSetCoins() {
        User user = new User();

        // Setze Coins und überprüfe den Wert
        user.setCoins(200);
        assertEquals(200, user.getCoins(), "Coins should be 200");

        // Setze Coins erneut und überprüfe den Wert
        user.setCoins(500);
        assertEquals(500, user.getCoins(), "Coins should be 500");
    }

    @Test
    void testDefaultUser() {
        User user = new User();

        // Überprüfe, dass der Benutzer ohne explizite Werte mit Standardwerten erstellt wird
        assertNull(user.getUserId(), "User ID should be null");
        assertNull(user.getUsername(), "Username should be null");
        assertNull(user.getPassword(), "Password should be null");
        assertNull(user.getToken(), "Token should be null");
        assertNull(user.getCoins(), "Coins should be null");
        assertNull(user.getName(), "Name should be null");
        assertNull(user.getBio(), "Bio should be null");
        assertNull(user.getImage(), "Image should be null");
    }

}
