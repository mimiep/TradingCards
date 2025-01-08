package org.example.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.example.logic.*;
import org.example.models.*;
import org.example.service.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.SQLException;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final UserService userService;
    private final DeckService deckService;
    private final CardService cardService;
    private final PackageService packageService;
    private final ScoreboardService scoreboardService;
    private final BattleService battleService;
    private final SendService sendService;
    private final ObjectMapper objectMapper;

    public RequestHandler(Socket socket, UserService userService, DeckService deckService, CardService cardService, PackageService packageService, ScoreboardService scoreboardService, BattleService battleService, SendService sendService) {
        this.socket = socket;
        this.userService = userService;
        this.deckService = deckService;
        this.cardService = cardService;
        this.packageService = packageService;
        this.scoreboardService = scoreboardService;
        this.battleService = battleService;
        this.sendService = sendService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String firstLine = in.readLine();
            System.out.println("Request: " + firstLine);

            if (firstLine.startsWith("POST /users")) {
                userService.handleUserRegistration(in, out);
            } else if (firstLine.startsWith("POST /sessions")) {
                userService.handleUserLogin(in, out);
            } else if (firstLine.startsWith("PUT /users")) {
                String username = firstLine.split("/")[2].split(" ")[0];
                userService.handleUpdateUser(in, out, username);
            } else if (firstLine.startsWith("GET /users")) {
                String username = firstLine.split("/")[2].split(" ")[0];
                userService.handleGetUser(in, out, username);
            } else if (firstLine.startsWith("PUT /deck")) {
                deckService.handleAddCardToDeck(in, out);
            } else if (firstLine.startsWith("GET /deck?format=plain")) {
                deckService.handleGetDeck2(in, out);
            } else if (firstLine.startsWith("GET /deck")) {
                deckService.handleGetDeck(in, out);
            } else if (firstLine.startsWith("POST /cards")) {
                cardService.handleCreateCard(in, out);
            } else if (firstLine.startsWith("GET /cards")) {
                cardService.handleGetCardsByUser(firstLine,in, out);
            } else if (firstLine.startsWith("POST /packages")) {
                packageService.handleCreatePackage(in, out);
            } else if (firstLine.startsWith("POST /transactions/packages")) {
                packageService.handleTransactionPackage(in, out);
            } else if (firstLine.startsWith("GET /stats")) {
                scoreboardService.handleStats(in, out);
            } else if (firstLine.startsWith("GET /scoreboard")) {
                scoreboardService.handleScoreboard(in, out);
            } else if (firstLine.startsWith("POST /battles")) {
                battleService.handleBattle(in, out);
            } else {    //falls ein Fehler auftritt und er die Methode nicht erkennt
                sendService.sendResponse(out, 405, "Method Not Allowed", "Methode nicht erlaubt." + "\r\n");
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
}


