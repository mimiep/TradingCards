package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.logic.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.Card;
import org.example.models.MonsterCard;
import org.example.models.SpellCard;
import org.example.models.User;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PackageService {

    private PackageLogic packageLogic;
    private CardLogic cardLogic;
    private UserLogic userLogic;
    private ObjectMapper objectMapper;
    private SendService sendService;

    public PackageService() {
        this.packageLogic = new PackageLogic();
        this.cardLogic = new CardLogic();
        this.userLogic = new UserLogic();
        this.sendService = new SendService();
        this.objectMapper = new ObjectMapper();
    }


    public void handleCreatePackage(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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

        try {

            // Kartenliste aus dem JSON-Request extrahieren
            List<Map<String, Object>> cardsList = objectMapper.readValue(requestBody.toString(), new TypeReference<List<Map<String, Object>>>() {
            });

            List<Card> cards = new ArrayList<>();

            for (Map<String, Object> cardData : cardsList) {
                String name = (String) cardData.get("Name");
                UUID id = UUID.fromString((String) cardData.get("Id"));
                double damage = (double) cardData.get("Damage");

                String type;
                if (name.contains("Spell")) {
                    type = "Spell";
                } else {
                    type = "Monster";
                }

                String elementType;
                if (name.contains("Water")) {
                    elementType = "Water";
                } else if (name.contains("Fire")) {
                    elementType = "Fire";
                } else if (name.contains("Regular")) {
                    elementType = "Normal";
                } else {
                    elementType = "Normal"; // Wenn kein Elementtyp erkennbar ist
                }

                // Überprüfen, ob es sich um eine MonsterCard oder SpellCard handelt
                if (name.endsWith("Spell")) {
                    // Erstelle eine SpellCard
                    cards.add(new SpellCard(id, name, (int) damage, type, elementType, UUID.randomUUID(), UUID.randomUUID()));
                } else {
                    // Erstelle eine MonsterCard
                    cards.add(new MonsterCard(id, name, (int) damage, type, elementType, UUID.randomUUID(), UUID.randomUUID()));
                }
            }

            List<Card> processedCards = cards.stream()
                    .map(card -> {
                        // Beispielhafte Typbestimmung basierend auf dem Namen der Karte
                        if (card.getName().endsWith("Spell")) {
                            return new SpellCard(card.getCardId(), card.getName(), card.getDamage(), card.getType(), card.getElementType(), card.getPackageId(), card.getUserId());
                        } else {
                            return new MonsterCard(card.getCardId(), card.getName(), card.getDamage(), card.getType(), card.getElementType(), card.getPackageId(), card.getUserId());
                        }
                    })
                    .collect(Collectors.toList());

            System.out.println(processedCards);

            // Paket erstellen und Karten speichern
            UUID packageId = packageLogic.createPackageList(processedCards);

            // Erfolgsmeldung zurückgeben
            String jsonResponse = "{\"packageId\":\"" + packageId.toString() + "} \r\n";

            sendService.sendResponse(out, 201, "Created", jsonResponse);

        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }
    public void handleTransactionPackage(BufferedReader in, BufferedWriter out) throws IOException {
        String token = null;

        // Token aus dem Header extrahieren
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim(); // "Bearer kienboec-mtcgToken"
            }
        }

        if (token == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"No token provided.\"}" + "\r\n");
            return;
        }

        try {
            // Benutzer validieren und Coins überprüfen
            User user = userLogic.getUserByToken(token);

            if (user == null) {
                sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid token.\"}"+ "\r\n");
                return;
            }

            if (user.getCoins() < 5) {
                sendService.sendResponse(out, 403, "Forbidden", "{\"message\":\"Not enough coins.\"}" + "\r\n");
                return;
            }

            // Paket erwerben
            PackageLogic packageLogic = new PackageLogic();
            boolean success = packageLogic.acquirePackage(user.getUserId());

            if (!success) {
                sendService.sendResponse(out, 404, "Not Found", "{\"message\":\"No packages available.\"}" + "\r\n");
                return;
            }

            // Coins abziehen
            userLogic.deductCoins(user.getUserId(), 5);

            // Erfolgreiche Antwort senden
            sendService.sendResponse(out, 201, "Created", "{\"message\":\"Package acquired successfully.\"}" + "\r\n");

        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }

    }


}
