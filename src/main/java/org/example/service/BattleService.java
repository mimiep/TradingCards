package org.example.service;

import org.example.models.User;
import org.example.logic.*;
import org.example.models.*;
import org.example.service.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;

public class BattleService {

    private BattleLogic battleLogic;
    private UserLogic userLogic;
    private SendService sendService;

    public BattleService() {
        this.battleLogic = new BattleLogic();
        this.userLogic = new UserLogic();
        this.sendService = new SendService();
    }



    public void handleBattle(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        String line;
        String authToken = null;

        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Authorization:")) {
                authToken = line.split(" ")[2].trim();
            }
        }

        if (authToken == null) {
            sendService.sendResponse(out, 401, "Unauthorized", "{\"message\":\"Authorization token fehlt.\"}" + "\r\n");
            return;
        }

        User user = userLogic.getUserByToken(authToken);

        battleLogic.addPlayer(user);

        if (battleLogic.getPlayers() > 2){
            sendService.sendResponse(out, 200, "OK", "{\"message\":\"Already enough players.\"}" + "\r\n");

        }
        else if(battleLogic.getPlayers() < 2){
            sendService.sendResponse(out, 200, "OK", "{\"message\":\"Player joined.\"}"+ "\r\n");
        }

        else {
            battleLogic.startBattle();
        }

        sendService.sendResponse(out, 201, "OK", "{\"message\":\"Battle finished.\"}"+ "\r\n");
    }

}
