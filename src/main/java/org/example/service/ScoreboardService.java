package org.example.service;

import org.example.models.ScoreboardEntry;
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

//Für Request, Scoreboard betreffend zuständig
public class ScoreboardService {

    private ScoreboardLogic scoreboardLogic;
    private UserLogic userLogic;
    private ObjectMapper objectMapper;
    private SendService sendService;

    public ScoreboardService() {
        this.scoreboardLogic = new ScoreboardLogic();
        this.userLogic = new UserLogic();
        this.sendService = new SendService();
        this.objectMapper = new ObjectMapper();
    }

    //Muss für Authentification ausgeführt werden bevor ELO zurückgegeben wird
    public void handleStats(BufferedReader in, BufferedWriter out) throws IOException {
        String line;
        String token = null;

        // Token extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                token = line.split(" ")[2].trim();  // Token aus dem Header extrahieren
            }
        }

        if (token == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid or missing token.\"}"+ "\r\n");
            return;
        }

        try {
            System.out.println("TOKEN"+ token);
            UUID userId = userLogic.getUserIdFromToken(token);
            System.out.println("USERID"+ userId);
            if (userId == null) {
                sendService.sendResponse(out, 403, "Forbidden", "{\"message\":\"User not authorized.\"}"+ "\r\n");
                return;
            }

            // Benutzerstatistik abrufen
            int elo = scoreboardLogic.getUserElo(userId);
            System.out.println("ELO"+ elo);

            sendService.sendResponse(out, 200, "OK", "{\"elo\":" + elo + "}" + "\r\n");
        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    //Muss für Authentification ausgeführt werden bevor Scoreboard zurückgegeben werden kann
    public void handleScoreboard(BufferedReader in, BufferedWriter out) throws IOException {
        String line;
        String authToken = null;

        // Token extrahieren
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                authToken = line.split(" ")[2].trim();
            }
        }

        if (authToken == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}" + "\r\n");
            return;
        }

        try {
            // Scoreboard abrufen
            List<ScoreboardEntry> scoreboard = scoreboardLogic.getScoreboard();
            String responseBody = objectMapper.writeValueAsString(scoreboard);
            sendService.sendResponse(out, 200, "OK", responseBody + "\r\n");
        } catch (SQLException e) {
            sendService.sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }



}
