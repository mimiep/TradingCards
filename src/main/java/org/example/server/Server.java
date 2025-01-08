package org.example.server;

//import org.example.logic.*;
import org.example.service.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService threadPool;
    private final UserService userService; // UserLogic hinzufügen
    private final DeckService deckService;
    private final CardService cardService;
    private final PackageService packageService;
    private final ScoreboardService scoreboardService;
    private final BattleService battleService;
    private final SendService sendService;

    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10); // Pool mit 10 Threads für mehrere Abfragen
        this.userService = new UserService();
        this.deckService = new DeckService();
        this.cardService = new CardService();
        this.packageService = new PackageService();
        this.scoreboardService = new ScoreboardService();
        this.sendService = new SendService();
        this.battleService = new BattleService();

    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                //System.out.println("New client connected");
                RequestHandler requestHandler = new RequestHandler(socket, userService, deckService, cardService, packageService, scoreboardService, battleService, sendService);
                threadPool.execute(requestHandler); //Request mit Thread machen
            }
        } catch (IOException e) {
            System.out.println("Error in the server: " + e.getMessage());
            e.printStackTrace(); //Genauere Angabe
        } finally {
            threadPool.shutdown(); // Thread-Pool "herunterfahren"
        }
    }

    public static void main(String[] args) {
        int port = 10001;
        Server server = new Server(port);
        server.start();
    }
}
