package org.example.service;

import org.example.models.User;
import org.example.logic.*;
import org.example.models.*;
import org.example.service.*;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.sql.SQLException;

//Für Request, Battle betreffend zuständig
public class BattleService {

    private BattleLogic battleLogic;
    private UserLogic userLogic;
    private SendService sendService;

    public BattleService() {
        this.battleLogic = new BattleLogic();
        this.userLogic = new UserLogic();
        this.sendService = new SendService();
    }

    //Wird von RequestHandler aufgerufen und muss durchgeführt werden bevor battle gestartet werden kann
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
            sendService.sendResponse(out, 200, "OK", "{\"message\":\"One Player.\"}" + "\r\n" +
                    "   O       \r\n" +
                    "  /|\\     \r\n" +
                    "  / \\    \r\n");
        }

        else {
            battleLogic.startBattle();
        }

        sendService.sendResponse(out, 201, "OK",
                "{\"message\":\"Two Player.\"}" + "\r\n" +
                        "   O     O    \r\n" +
                        "  /|\\   /|\\  \r\n" +
                        "  / \\   / \\  \r\n"
        );
    }

}
