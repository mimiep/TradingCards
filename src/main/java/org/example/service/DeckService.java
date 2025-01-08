package org.example.service;

import org.example.logic.*;
import org.example.models.*;
import org.example.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Für Request, Deck betreffend zuständig
public class DeckService {

    private UserLogic userLogic;
    private DeckLogic deckLogic;
    private CardLogic cardLogic;
    private ObjectMapper objectMapper;
    private SendService sendService;

    public DeckService() {
        this.userLogic = new UserLogic();
        this.deckLogic = new DeckLogic();
        this.cardLogic = new CardLogic();
        this.sendService = new SendService();
        this.objectMapper = new ObjectMapper();
    }

    //muss ausgeführt werden damit man Authentification usw. hat und dann gibt man Werte weiter
    public void handleAddCardToDeck(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String token = null;
        String line;
        int contentLength = 0;

        // Header lesen, um Token und Content-Length zu extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim(); // Token extrahieren
            } else if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Kein Token vorhanden
        if (token == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }

        //Body einlesen
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            requestBody.append(bodyChars);
        }

        try {
            // Karten-IDs aus JSON-Body parsen
            List<String> cardIdStrings = objectMapper.readValue(requestBody.toString(), List.class);

            List<UUID> cardIds = new ArrayList<>();
            for (String id : cardIdStrings) {
                cardIds.add(UUID.fromString(id));
            }


            // Validieren: Genau 4 Karten erforderlich
            if (cardIds.size() != 4) {
                sendService.sendResponse(out, 400, "Bad Request", "{\"message\":\"Genau 4 Karten erforderlich.\"}"+ "\r\n");
                return;
            }

            // Benutzer-ID anhand des Tokens abrufen
            UUID userId = userLogic.getUserIdFromToken(token);

            if (userId == null) {
                sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Ungültiges Token.\"}"+ "\r\n");
                return;
            }

            // Prüfen, ob der Benutzer bereits ein Deck hat
            List<UUID> existingDeck = deckLogic.getDeck(userId);

            if (!existingDeck.isEmpty()) {
                sendService.sendResponse(out, 400, "Bad Request", "{\"message\":\"Ein Deck ist bereits konfiguriert.\"}"+ "\r\n");
                return;
            }


            for (UUID cardId : cardIds) {
                boolean belongsToUser = cardLogic.belongToUser(userId, cardId);  // Verwende die neue belongToUser Methode
                if (!belongsToUser) {
                    sendService.sendResponse(out, 403, "Forbidden", "{\"message\":\"Eine oder mehrere Karten gehören nicht zum Benutzer.\"}"+ "\r\n");
                    return;
                }
            }

            // Karten zum Deck hinzufügen
            for (UUID cardId : cardIds) {
                deckLogic.addCardToDeck(userId, cardId);
            }

            // Erfolgsmeldung senden
            sendService.sendResponse(out, 200, "OK", "{\"message\":\"Deck erfolgreich konfiguriert.\"}"+ "\r\n");
        } catch (IllegalArgumentException e) {
            sendService.sendResponse(out, 400, "Bad Request", "{\"message\":\"Ungültiger Karten-UUID im Body.\"}"+ "\r\n");
        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendService.sendResponse(out, 400, "Bad Request", "{\"message\":\"Ungültiger JSON-Body.\"}"+ "\r\n");
        }
    }

    //Ausgabe von Deck
    public void handleGetDeck(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        String line;
        String token = null;

        // Weiter mit den Headers, um das Token zu extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim();  // Token aus dem Header extrahieren
            }
        }

        if (token == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }

        try {
            // Benutzer-ID basierend auf dem Token abrufen
            UUID userId = userLogic.getUserIdFromToken(token);
            if (userId == null) {
                sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Ungültiges Token.\"}"+ "\r\n");
                return;
            }

            List<UUID> deck = deckLogic.getDeck(userId);

            String responseBody = objectMapper.writeValueAsString(deck);
            sendService.sendResponse(out, 200, "OK", responseBody + "\r\n");

        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }

    }

    //das selbe wie oben nur "schöner" ausgegeben
    public void handleGetDeck2(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        String line;
        String token = null;
        String format = "json";
        line = in.readLine();

        System.out.println("PLAIN ERKANNT");

        // Weiter mit den Headers, um das Token zu extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim();  // Token aus dem Header extrahieren
            }
        }

        if (token == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }

        try {
            // Benutzer-ID basierend auf dem Token abrufen
            UUID userId = userLogic.getUserIdFromToken(token);
            if (userId == null) {
                sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Ungültiges Token.\"}"+ "\r\n");
                return;
            }

            List<UUID> deck = deckLogic.getDeck(userId);

            // Alternative Darstellung im Klartext
            StringBuilder plainTextDeck = new StringBuilder();
            plainTextDeck.append("User's Deck:\n");
            for (UUID cardId : deck) {
                plainTextDeck.append("- ").append(cardId.toString()).append("\n");
            }
            sendService.sendResponse(out, 200, "OK", plainTextDeck.toString());

        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }

    }

}
