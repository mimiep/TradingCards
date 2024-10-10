package org.example;


import org.example.server.Server;

public class Main {
    public static void main(String[] args) {
        // Server-Instanz erstellen und starten
        Server server = new Server(10001); // Port 10001 verwenden
        server.start(); // Server starten
    }
}