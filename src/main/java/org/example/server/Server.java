package org.example.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService threadPool;

    public Server(int port) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(10); // Thread-Pool mit 10 Threads
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");
                RequestHandler requestHandler = new RequestHandler(socket);
                threadPool.execute(requestHandler); // handle request with a thread from the pool
            }
        } catch (IOException e) {
            System.out.println("Error in the server: " + e.getMessage());
            e.printStackTrace(); // Mehr Kontext für das Debugging
        } finally {
            threadPool.shutdown(); // Thread-Pool sauber herunterfahren
        }
    }

    public static void main(String[] args) {
        int port = 10001; // Port für den Server
        Server server = new Server(port);
        server.start();
    }
}
