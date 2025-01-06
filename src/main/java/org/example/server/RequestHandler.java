package org.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.models.*;
import org.example.models.Package;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.UUID;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final UserLogic userLogic;
    private final DeckLogic deckLogic;
    private final CardLogic cardLogic;
    private final PackageLogic packageLogic;
    private final ObjectMapper objectMapper;

    public RequestHandler(Socket socket, UserLogic userLogic, DeckLogic deckLogic, CardLogic cardLogic, PackageLogic packageLogic) {
        this.socket = socket;
        this.userLogic = userLogic;
        this.deckLogic = deckLogic;
        this.cardLogic = cardLogic;
        this.packageLogic = packageLogic;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String firstLine = in.readLine();
            System.out.println("Request: " + firstLine);

            if (firstLine.startsWith("POST /users")) {
                handleUserRegistration(in, out);
            } else if (firstLine.startsWith("POST /sessions")) {
                handleUserLogin(in, out);
            } else if (firstLine.startsWith("POST /decks")) {
                handleAddCardToDeck(in, out);
            } else if (firstLine.startsWith("GET /decks")) {
                handleGetDeck(in, out);
            } else if (firstLine.startsWith("POST /cards")) {
                handleCreateCard(in, out);
            } else if (firstLine.startsWith("GET /cards")) {
                handleGetCardsByUser(firstLine,in, out);
            } else if (firstLine.startsWith("POST /transactions/packages")) {
                handleTransactionPackage(in, out);
                sendResponse(out, 405, "Derzeitige Fehlerbehebung", "Derzeitige Fehlerbehebung");
            } else if (firstLine.startsWith("POST /packages")) {
                handleCreatePackage(in, out);
            }
            //Zuvor
            else {    //praktisch fürs CURL, er lässt somit die anderen Test noch nicht durch
                sendResponse(out, 405, "Method Not Allowed", "Methode nicht erlaubt.");
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    //Funktionen auslagern, weil jetzt ist es so überfüllt
    private void handleUserRegistration(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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

        // JSON in User-Objekt umwandeln
        User user = objectMapper.readValue(requestBody.toString(), User.class);

        // Benutzer registrieren
        try {
            boolean registrationSuccessful = userLogic.registerUser(user.getUsername(), user.getPassword());
            if (registrationSuccessful) {
                sendResponse(out, 201, "Created", "{\"message\":\"User registered successfully.\"}");
            } else {
                sendResponse(out, 409, "Conflict", "{\"message\":\"User already exists.\"}");
            }
        } catch (SQLException e) {

            if (e.getSQLState().equals("23505")) { // 23505 ist der SQL-State für Unique-Constraint-Verletzungen in PostgreSQL
                sendResponse(out, 409, "Conflict", "{\"message\":\"User already exists.\"}");
            } else {
                sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
            }

        }
    }

    private void handleUserLogin(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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

        // JSON in User-Objekt umwandeln
        User user = objectMapper.readValue(requestBody.toString(), User.class);

        // Benutzer einloggen und Token generieren
        try {
            String token = userLogic.loginUser(user.getUsername(), user.getPassword());
            if (token != null) {
                String jsonResponse = "{\"token\":\"" + token + "\"}";
                sendResponse(out, 200, "OK", jsonResponse);
            } else {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid login credentials.\"}");
            }
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleAddCardToDeck(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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

        // JSON in Deck-Objekt umwandeln
        Deck deck = objectMapper.readValue(requestBody.toString(), Deck.class);

        // Karte zum Deck hinzufügen
        try {
            deckLogic.addCardToDeck(deck.getUserId(), deck.getCardId());
            sendResponse(out, 201, "Created", "{\"message\":\"Card added to deck successfully.\"}");
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetDeck(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;

        // Header lesen, um Content-Length zu bestimmen
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Den Body lesen (optional, je nach dem wie der User den Request sendet, kann es auch ohne Body sein)
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            requestBody.append(bodyChars);
        }

        // Falls ein Body vorhanden ist, das User-Objekt extrahieren
        Deck deckRequest = objectMapper.readValue(requestBody.toString(), Deck.class);

        // Deck des Benutzers abrufen
        try {
            List<UUID> deck = deckLogic.getDeck(deckRequest.getUserId());
            String responseBody = objectMapper.writeValueAsString(deck);
            sendResponse(out, 200, "OK", responseBody);
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleCreateCard(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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
            sendResponse(out, 201, "Created", "{\"message\":\"Card created successfully.\"}");
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetCardsByUser(String firstLine, BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;

        if (!firstLine.startsWith("GET")) {
            sendResponse(out, 405, "Method Not Allowed", "GET erforderlich.");
            return;
        }

        // Header lesen, um Content-Length zu bestimmen
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Den Body lesen (optional, je nach dem wie der User den Request sendet, kann es auch ohne Body sein)
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            requestBody.append(bodyChars);
        }

        // Falls ein Body vorhanden ist, das User-Objekt extrahieren
        String[] requestParts = firstLine.split(" ");
        UUID userId = UUID.fromString(requestParts[1].split("/")[2]);

        // Karten des Benutzers abrufen
        try {
            List<Card> cards = cardLogic.getCardsByUser(userId);
            String responseBody = objectMapper.writeValueAsString(cards);
            sendResponse(out, 200, "OK", responseBody);
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleCreatePackage(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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

        System.out.println("Content-Length: " + contentLength);
        System.out.println("Request Body: " + requestBody);

        System.out.println("Fertig LOL");

        try {

            // Kartenliste aus dem JSON-Request extrahieren
            //Problem
            System.out.println("-----------------PROBIEREN DES ARRAYS----------------");
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
                } else if (name.contains("Monster")) {
                    type = "Monster";
                } else {
                    type = "Normal"; // Weder Spell noch Monster
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
            String jsonResponse = "{\"packageId\":\"" + packageId.toString() + "\"}";

            sendResponse(out, 201, "Created", jsonResponse);

        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }
    private void handleTransactionPackage(BufferedReader in, BufferedWriter out) throws IOException {
        String token = null;

        // Token aus dem Header extrahieren
        String line;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim(); // "Bearer kienboec-mtcgToken"
            }
        }

        if (token == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"No token provided.\"}");
            return;
        }

        try {
            // Benutzer validieren und Coins überprüfen
            UserLogic userLogic = new UserLogic();
            User user = userLogic.getUserByToken(token);

            if (user == null) {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid token.\"}");
                return;
            }

            if (user.getCoins() < 5) {
                sendResponse(out, 403, "Forbidden", "{\"message\":\"Not enough coins.\"}");
                return;
            }

            // Paket erwerben
            PackageLogic packageLogic = new PackageLogic();
            boolean success = packageLogic.acquirePackage(user.getUserId());

            if (!success) {
                sendResponse(out, 404, "Not Found", "{\"message\":\"No packages available.\"}");
                return;
            }

            // Coins abziehen
            userLogic.deductCoins(user.getUserId(), 5);

            // Erfolgreiche Antwort senden
            sendResponse(out, 201, "Created", "{\"message\":\"Package acquired successfully.\"}");

        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }




    }


    // HTTP-Response senden
    private void sendResponse(BufferedWriter out, int statusCode, String statusMessage, String responseBody) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "\r\n" +
                responseBody;

        out.write(response);
        out.flush();
    }
}


