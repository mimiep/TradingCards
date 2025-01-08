package server;

import org.example.server.RequestHandler;
import org.example.service.*;
import org.example.logic.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.io.*;
import static org.mockito.Mockito.*;

import java.io.*;
import java.net.Socket;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class RequestHandlerTest {

    @Test
    void testMethodNotAllowed() throws IOException {
        // Simuliere eine HTTP-Anfrage mit einer nicht erlaubten Methode
        String request = "PUT /invalidEndpoint HTTP/1.1\r\n";

        // Erstelle einen InputStream mit der simulierten Anfrage
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        OutputStream outputStream = new ByteArrayOutputStream();

        // Erstelle den gemockten Socket (mit simulierten Streams)
        Socket socket = new Socket() {
            @Override
            public InputStream getInputStream() {
                return inputStream;
            }

            @Override
            public OutputStream getOutputStream() {
                return outputStream;
            }
        };

        // Erstelle die notwendigen Service-Objekte
        UserService userService = new UserService();
        DeckService deckService = new DeckService();
        CardService cardService = new CardService();
        PackageService packageService = new PackageService();
        ScoreboardService scoreboardService = new ScoreboardService();
        BattleService battleService = new BattleService();
        SendService sendService = new SendService();

        // Erstelle den RequestHandler mit dem gemockten Socket, ich hasse Mocking so sehr
        RequestHandler requestHandler = new RequestHandler(socket, userService, deckService, cardService, packageService, scoreboardService, battleService, sendService);

        requestHandler.run();

        // Antwort aus OutputStream
        String response = outputStream.toString();

        assertTrue(response.contains("405"), "Response should contain 405");
        assertTrue(response.contains("Method Not Allowed"), "Response should contain 'Method Not Allowed'");
    }
}
