package org.example.service;

import org.example.logic.*;
import org.example.models.*;
import org.example.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

//Für Request, Card betreffend zuständig
public class CardService {

    private CardLogic cardLogic;
    private UserLogic userLogic;
    private ObjectMapper objectMapper;
    private SendService sendService;

    public CardService() {
        this.cardLogic = new CardLogic();
        this.userLogic = new UserLogic();
        this.sendService = new SendService();
        this.objectMapper = new ObjectMapper();
    }

    //muss ausgeführt werden damit man Authentification usw. hat und dann gibt man Werte weiter
    public void handleCreateCard(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;

        // Header lesen, um Content-Length zu bestimmen
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Den Body lesen
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            requestBody.append(bodyChars);
        }

        // JSON in Card-Objekt umwandeln
        Card card = objectMapper.readValue(requestBody.toString(), Card.class);

        // Karte erstellen
        try {
            cardLogic.createCard(card.getCardId(), card.getName(), card.getDamage(), card.getType(), card.getElementType(), card.getPackageId(), card.getUserId());
            sendService.sendResponse(out, 201, "Created", "{\"message\":\"Card created successfully.\"}" + "\r\n");
        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    public void handleGetCardsByUser(String firstLine, BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        String token = null;
        int contentLength = 0;

        if (!firstLine.startsWith("GET")) {
            sendService.sendResponse(out, 405, "Method Not Allowed", "{\"message\":\"GET necessary.\"} " + "\r\n");
            return;
        }

        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim(); // "Bearer kienboec-mtcgToken"
            }
        }

        if (token == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"No token provided.\"}"+ "\r\n");
            return;
        }

        UUID userId = userLogic.getUserIdFromToken(token);
        if (userId == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid token\"}"+ "\r\n");
            return;
        }

        try {
            List<Card> cards = cardLogic.getCardsByUser(userId);

            // Wenn keine Karten gefunden werden
            if (cards.isEmpty()) {
                sendService.sendResponse(out, 200, "OK", "{\"message\":\"No cards found\", \"cards\":[]}"+ "\r\n");
                return;
            }

            // Karten in JSON formatieren und zurückgeben
            String responseBody = objectMapper.writeValueAsString(cards);
            sendService.sendResponse(out, 200, "OK", responseBody + "\r\n");

        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }

    }


}
