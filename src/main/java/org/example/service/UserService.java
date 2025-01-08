package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.logic.*;
import org.example.models.*;
import org.example.service.*;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

//Für Request, User betreffend zuständig
public class UserService {

    private UserLogic userLogic;
    private ScoreboardLogic scoreboardLogic;
    private ObjectMapper objectMapper;
    private SendService sendService;

    public UserService() {
        this.userLogic = new UserLogic();
        this.scoreboardLogic = new ScoreboardLogic();
        this.sendService = new SendService();
        this.objectMapper = new ObjectMapper();
    }

    //Teilen des Headers um zu wissen was zu tun, um dann User zu registieren
    public void handleUserRegistration(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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

                sendService.sendResponse(out, 201, "Created", "{\"message\":\"User registered successfully.\"}"+ "\r\n");
            } else {
                sendService.sendResponse(out, 409, "Conflict", "{\"message\":\"User already exists.\"}"+ "\r\n");
            }
        } catch (SQLException e) {

            if (e.getSQLState().equals("23505")) { // 23505 ist der SQL-State für Unique-Constraint-Verletzungen in PostgreSQL
                sendService.sendResponse(out, 409, "Conflict", "{\"message\":\"User already exists.\"}"+ "\r\n");
            } else {
                sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
            }

        }
    }

    //Einloggen von Benutzer
    public void handleUserLogin(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
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
                sendService.sendResponse(out, 200, "OK", jsonResponse + "\r\n");
            } else {
                sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid login credentials.\"}"+ "\r\n");
            }
        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    //Für Authentification ausführen, bevor User ausgegeben werden darf
    public void handleGetUser(BufferedReader in, BufferedWriter out, String username) throws IOException {
        String token = null;
        String line;

        // Token extrahieren
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
            if (!token.equals(userLogic.generateToken(username))) {
                System.out.println("TOKEN passt nicht weil"+token+ "UNGLEICH "+ username);
                sendService.sendResponse(out, 403, "Forbidden", "{\"message\":\"No Authorization for that user\"}"+ "\r\n");
                return;
            }
            // Benutzerinformationen abrufen
            User user = userLogic.getUserByUsername(username);
            if (user == null) {
                sendService.sendResponse(out, 404, "Not Found", "{\"message\":\"User not found.\"}"+ "\r\n");
                return;
            }

            // Benutzerdaten im JSON-Format zurückgeben
            String responseBody = objectMapper.writeValueAsString(user);
            sendService.sendResponse(out, 200, "OK", responseBody + "\r\n");
        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    //Muss für Authentification ausgeführt werden bevor man Dinge ändern kann
    public void handleUpdateUser(BufferedReader in, BufferedWriter out, String username) throws IOException {
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
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}"+ "\r\n");
            return;
        }

        try {
            // Benutzer-ID aus Token extrahieren
            UUID userId = userLogic.getUserIdFromToken(token);

            if (userId == null || !username.equals(userLogic.getUsernameFromId(userId))) {
                sendService.sendResponse(out, 403, "Forbidden", "{\"message\":\"You are not authorized to edit this user's data.\"}"+ "\r\n");
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
                sendService.sendResponse(out, 200, "OK", "{\"message\":\"User updated successfully.\"}"+ "\r\n");
            } else {
                sendService.sendResponse(out, 400, "Bad Request", "{\"message\":\"Error updating user.\"}"+ "\r\n");
            }
        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }



}
