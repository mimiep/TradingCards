package org.example.server;

import org.example.models.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService threadPool;
    private final UserLogic userLogic; // UserLogic hinzufügen
    private final DeckLogic deckLogic;
    private final CardLogic cardLogic;
    private final PackageLogic packageLogic;
    private final ScoreboardLogic scoreboardLogic;
    private final BattleLogic battleLogic;

    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10); // Pool mit 10 Threads für mehrere Abfragen
        this.userLogic = new UserLogic();
        this.deckLogic = new DeckLogic();
        this.cardLogic = new CardLogic();
        this.packageLogic = new PackageLogic();
        this.scoreboardLogic = new ScoreboardLogic();
        this.battleLogic = new BattleLogic();

    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                //System.out.println("New client connected");
                RequestHandler requestHandler = new RequestHandler(socket, userLogic, deckLogic, cardLogic, packageLogic, scoreboardLogic, battleLogic);
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
