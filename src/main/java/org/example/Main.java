package org.example;

import org.example.server.Server;
import org.example.database.Database;

public class Main {
    public static void main(String[] args) {

        Database db = new Database(); //Datenbank-Instanz
        db.testConnection();

        Server server = new Server(10001); // Port 10001 verwenden
        server.start(); // Server starten
    }
}