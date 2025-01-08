package org.example.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.models.*;


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
    private final ScoreboardLogic scoreboardLogic;
    private final BattleLogic battleLogic;
    private final ObjectMapper objectMapper;

    public RequestHandler(Socket socket, UserLogic userLogic, DeckLogic deckLogic, CardLogic cardLogic, PackageLogic packageLogic, ScoreboardLogic scoreboardLogic, BattleLogic battleLogic) {
        this.socket = socket;
        this.userLogic = userLogic;
        this.deckLogic = deckLogic;
        this.cardLogic = cardLogic;
        this.packageLogic = packageLogic;
        this.scoreboardLogic = scoreboardLogic;
        this.battleLogic = battleLogic;
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
            } else if (firstLine.startsWith("PUT /users")) {
                String username = firstLine.split("/")[2].split(" ")[0];
                handleUpdateUser(in, out, username);
            } else if (firstLine.startsWith("GET /users")) {
                String username = firstLine.split("/")[2].split(" ")[0];
                handleGetUser(in, out, username);
            } else if (firstLine.startsWith("PUT /deck")) {
                handleAddCardToDeck(in, out);
            } else if (firstLine.startsWith("GET /deck?format=plain")) {
                handleGetDeck2(in, out);
            } else if (firstLine.startsWith("GET /deck")) {
                handleGetDeck(in, out);
            } else if (firstLine.startsWith("POST /cards")) {
                handleCreateCard(in, out);
            } else if (firstLine.startsWith("GET /cards")) {
                handleGetCardsByUser(firstLine,in, out);
            } else if (firstLine.startsWith("POST /packages")) {
                handleCreatePackage(in, out);
            } else if (firstLine.startsWith("POST /transactions/packages")) {
                handleTransactionPackage(in, out);
            } else if (firstLine.startsWith("GET /stats")) {
                handleStats(in, out);
            } else if (firstLine.startsWith("GET /scoreboard")) {
                handleScoreboard(in, out);
            } else if (firstLine.startsWith("POST /battles")) {
                handleBattle(in, out);
            } else {    //falls ein Fehler auftritt und er die Methode nicht erkennt
                sendResponse(out, 405, "Method Not Allowed", "Methode nicht erlaubt." + "\r\n");
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
                UUID userId = userLogic.getUserIdByUsername(user.getUsername()); // Hier müsstest du eine Methode haben, die die User-ID anhand des Usernamens zurückgibt

                // Füge den neuen Benutzer ins Scoreboard ein
                scoreboardLogic.insertUserScoreboard(userId);

                sendResponse(out, 201, "Created", "{\"message\":\"User registered successfully.\"}"+ "\r\n");
            } else {
                sendResponse(out, 409, "Conflict", "{\"message\":\"User already exists.\"}"+ "\r\n");
            }
        } catch (SQLException e) {

            if (e.getSQLState().equals("23505")) { // 23505 ist der SQL-State für Unique-Constraint-Verletzungen in PostgreSQL
                sendResponse(out, 409, "Conflict", "{\"message\":\"User already exists.\"}"+ "\r\n");
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
                sendResponse(out, 200, "OK", jsonResponse + "\r\n");
            } else {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid login credentials.\"}"+ "\r\n");
            }
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetUser(BufferedReader in, BufferedWriter out, String username) throws IOException {
        String token = null;
        String line;

        // Token extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim();  // Token aus dem Header extrahieren
            }
        }

        if (token == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }



        try {
            if (!token.equals(userLogic.generateToken(username))) {
                System.out.println("TOKEN passt nicht weil"+token+ "UNGLEICH "+ username);
                sendResponse(out, 403, "Forbidden", "{\"message\":\"No Authorization for that user\"}"+ "\r\n");
                return;
            }
            // Benutzerinformationen abrufen
            User user = userLogic.getUserByUsername(username);
            if (user == null) {
                sendResponse(out, 404, "Not Found", "{\"message\":\"User not found.\"}"+ "\r\n");
                return;
            }

            // Benutzerdaten im JSON-Format zurückgeben
            String responseBody = objectMapper.writeValueAsString(user);
            sendResponse(out, 200, "OK", responseBody + "\r\n");
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleUpdateUser(BufferedReader in, BufferedWriter out, String username) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        String token = null;
        int contentLength = 0;

        // Header lesen, um Content-Length und Authorization-Token zu bestimmen
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim();
            } else if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Den Body lesen
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            requestBody.append(bodyChars);
        }

        if (token == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }

        try {
            // Benutzer-ID aus Token extrahieren
            UUID userId = userLogic.getUserIdFromToken(token);

            if (userId == null || !username.equals(userLogic.getUsernameFromId(userId))) {
                sendResponse(out, 403, "Forbidden", "{\"message\":\"You are not authorized to edit this user's data.\"}"+ "\r\n");
                return;
            }

            // JSON-Body analysieren
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode requestData = objectMapper.readTree(requestBody.toString());
            String name = requestData.has("Name") ? requestData.get("Name").asText() : null;
            String bio = requestData.has("Bio") ? requestData.get("Bio").asText() : null;
            String image = requestData.has("Image") ? requestData.get("Image").asText() : null;

            // Benutzerdaten aktualisieren
            boolean success = userLogic.updateUser(username, name, bio, image);
            if (success) {
                sendResponse(out, 200, "OK", "{\"message\":\"User updated successfully.\"}"+ "\r\n");
            } else {
                sendResponse(out, 400, "Bad Request", "{\"message\":\"Error updating user.\"}"+ "\r\n");
            }
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }




    private void handleAddCardToDeck(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
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
                sendResponse(out, 400, "Bad Request", "{\"message\":\"Genau 4 Karten erforderlich.\"}"+ "\r\n");
                return;
            }

            // Benutzer-ID anhand des Tokens abrufen
            UUID userId = userLogic.getUserIdFromToken(token);

            if (userId == null) {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Ungültiges Token.\"}"+ "\r\n");
                return;
            }

            // Prüfen, ob der Benutzer bereits ein Deck hat
            List<UUID> existingDeck = deckLogic.getDeck(userId);

            if (!existingDeck.isEmpty()) {
                sendResponse(out, 400, "Bad Request", "{\"message\":\"Ein Deck ist bereits konfiguriert.\"}"+ "\r\n");
                return;
            }


            for (UUID cardId : cardIds) {
                boolean belongsToUser = cardLogic.belongToUser(userId, cardId);  // Verwende die neue belongToUser Methode
                if (!belongsToUser) {
                    sendResponse(out, 403, "Forbidden", "{\"message\":\"Eine oder mehrere Karten gehören nicht zum Benutzer.\"}"+ "\r\n");
                    return;
                }
            }

            // Karten zum Deck hinzufügen
            for (UUID cardId : cardIds) {
                deckLogic.addCardToDeck(userId, cardId);
            }

            // Erfolgsmeldung senden
            sendResponse(out, 200, "OK", "{\"message\":\"Deck erfolgreich konfiguriert.\"}"+ "\r\n");
        } catch (IllegalArgumentException e) {
            sendResponse(out, 400, "Bad Request", "{\"message\":\"Ungültiger Karten-UUID im Body.\"}"+ "\r\n");
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            sendResponse(out, 400, "Bad Request", "{\"message\":\"Ungültiger JSON-Body.\"}"+ "\r\n");
        }
    }

    private void handleGetDeck(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        String line;
        String token = null;

        // Weiter mit den Headers, um das Token zu extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim();  // Token aus dem Header extrahieren
            }
        }

        if (token == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }

        try {
            // Benutzer-ID basierend auf dem Token abrufen
            UUID userId = userLogic.getUserIdFromToken(token);
            if (userId == null) {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Ungültiges Token.\"}"+ "\r\n");
                return;
            }

            List<UUID> deck = deckLogic.getDeck(userId);

                String responseBody = objectMapper.writeValueAsString(deck);
                sendResponse(out, 200, "OK", responseBody + "\r\n");

        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }

    }

    private void handleGetDeck2(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }

        try {
            // Benutzer-ID basierend auf dem Token abrufen
            UUID userId = userLogic.getUserIdFromToken(token);
            if (userId == null) {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Ungültiges Token.\"}"+ "\r\n");
                return;
            }

            List<UUID> deck = deckLogic.getDeck(userId);

                // Alternative Darstellung im Klartext
                StringBuilder plainTextDeck = new StringBuilder();
                plainTextDeck.append("User's Deck:\n");
                for (UUID cardId : deck) {
                    plainTextDeck.append("- ").append(cardId.toString()).append("\n");
                }
                sendResponse(out, 200, "OK", plainTextDeck.toString());

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
            sendResponse(out, 201, "Created", "{\"message\":\"Card created successfully.\"}" + "\r\n");
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleGetCardsByUser(String firstLine, BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        String token = null;
        int contentLength = 0;

        if (!firstLine.startsWith("GET")) {
            sendResponse(out, 405, "Method Not Allowed", "{\"message\":\"GET necessary.\"} " + "\r\n");
            return;
        }

        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim(); // "Bearer kienboec-mtcgToken"
            }
        }

        if (token == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"No token provided.\"}"+ "\r\n");
            return;
        }

        UUID userId = userLogic.getUserIdFromToken(token);
        if (userId == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid token\"}"+ "\r\n");
            return;
        }

        try {
            List<Card> cards = cardLogic.getCardsByUser(userId);

            // Wenn keine Karten gefunden werden
            if (cards.isEmpty()) {
                sendResponse(out, 200, "OK", "{\"message\":\"No cards found\", \"cards\":[]}"+ "\r\n");
                return;
            }

            // Karten in JSON formatieren und zurückgeben
            String responseBody = objectMapper.writeValueAsString(cards);
            sendResponse(out, 200, "OK", responseBody + "\r\n");

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
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"No token provided.\"}" + "\r\n");
            return;
        }

        try {
            // Benutzer validieren und Coins überprüfen
            User user = userLogic.getUserByToken(token);

            if (user == null) {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid token.\"}"+ "\r\n");
                return;
            }

            if (user.getCoins() < 5) {
                sendResponse(out, 403, "Forbidden", "{\"message\":\"Not enough coins.\"}" + "\r\n");
                return;
            }

            // Paket erwerben
            PackageLogic packageLogic = new PackageLogic();
            boolean success = packageLogic.acquirePackage(user.getUserId());

            if (!success) {
                sendResponse(out, 404, "Not Found", "{\"message\":\"No packages available.\"}" + "\r\n");
                return;
            }

            // Coins abziehen
            userLogic.deductCoins(user.getUserId(), 5);

            // Erfolgreiche Antwort senden
            sendResponse(out, 201, "Created", "{\"message\":\"Package acquired successfully.\"}" + "\r\n");

        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }

    }

    private void handleStats(BufferedReader in, BufferedWriter out) throws IOException {
        String line;
        String token = null;


        // Token extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim();  // Token aus dem Header extrahieren
            }
        }

        if (token == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid or missing token.\"}"+ "\r\n");
            return;
        }

        try {
            System.out.println("TOKEN"+ token);
            UUID userId = userLogic.getUserIdFromToken(token);
            System.out.println("USERID"+ userId);
            if (userId == null) {
                sendResponse(out, 403, "Forbidden", "{\"message\":\"User not authorized.\"}"+ "\r\n");
                return;
            }

            // Benutzerstatistik abrufen
            int elo = scoreboardLogic.getUserElo(userId);
            System.out.println("ELO"+ elo);

            sendResponse(out, 200, "OK", "{\"elo\":" + elo + "}" + "\r\n");
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleScoreboard(BufferedReader in, BufferedWriter out) throws IOException {
        String line;
        String authToken = null;

        // Token extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                authToken = line.split(" ")[2].trim();
            }
        }

        if (authToken == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}" + "\r\n");
            return;
        }

        try {
            // Scoreboard abrufen
            List<ScoreboardEntry> scoreboard = scoreboardLogic.getScoreboard();
            String responseBody = objectMapper.writeValueAsString(scoreboard);
            sendResponse(out, 200, "OK", responseBody + "\r\n");
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleBattle(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        String line;
        String authToken = null;

        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                authToken = line.split(" ")[2].trim();
            }
        }

        if (authToken == null) {
            sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}" + "\r\n");
            return;
        }

        User user = userLogic.getUserByToken(authToken);

            battleLogic.addPlayer(user);

            if (battleLogic.getPlayers() > 2){
                sendResponse(out, 200, "OK", "{\"message\":\"Already enough players.\"}" + "\r\n");

            }
            else if(battleLogic.getPlayers() < 2){
                sendResponse(out, 200, "OK", "{\"message\":\"Player joined.\"}"+ "\r\n");
            }

            else {
                battleLogic.startBattle();
            }

        sendResponse(out, 201, "OK", "{\"message\":\"Battle finished.\"}"+ "\r\n");
    }

    // HTTP-Response senden
    private void sendResponse(BufferedWriter out, int statusCode, String statusMessage, String responseBody) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "\r\n" +
                responseBody + "\r\n";

        out.write(response);
        out.flush();
    }
}


