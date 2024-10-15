package org.example;

import org.example.server.Server;
import org.example.database.Database;

public class Main {
    public static void main(String[] args) {
        // Server-Instanz erstellen und starten
        Database db = new Database();
        db.testConnection();

        Server server = new Server(10001); // Port 10001 verwenden
        server.start(); // Server starten
    }
}