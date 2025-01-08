package service;


import org.example.models.User;
import org.example.service.UserService;
import org.example.service.SendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserServiceTest {
    private UserService userService;
    private SendService sendService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        sendService = new SendService();
    }

    @Test
    void testHandleUserRegistration() throws IOException, SQLException {
        // Setup: HTTP-Request Header und Body
        String requestBody = "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}";
        String header = "Content-Type: application/json\r\nContent-Length: " + requestBody.length() + "\r\n";

        // BufferedReader simuliert die eingehende Anfrage
        ByteArrayInputStream inputStream = new ByteArrayInputStream((header + "\r\n" + requestBody).getBytes());
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

        // BufferedWriter simuliert die Antwort, die an den Client gesendet wird
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        BufferedWriter out = new BufferedWriter(charArrayWriter);

        // Simuliere die Benutzerregistrierung
        userService.handleUserRegistration(in, out);

        // Erwartete Antwort (aus dem curl-Beispiel)
        String expectedResponse = "HTTP/1.1 201 Created\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 45\r\n" +
                "\r\n" +
                "{\"message\":\"User registered successfully.\"}\r\n" +
                "\r\n";

        assertEquals(expectedResponse, charArrayWriter.toString());
    }


}
