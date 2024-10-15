package org.example.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.User;
import org.example.models.UserLogic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.sql.SQLException;

public class RequestHandler implements Runnable {
    private final Socket socket;
    private final UserLogic userLogic;
    private final ObjectMapper objectMapper;

    public RequestHandler(Socket socket, UserLogic userLogic) {
        this.socket = socket;
        this.userLogic = userLogic;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String firstLine = in.readLine();
            System.out.println("Request: " + firstLine);

            // POST für User Registrierung
            if (firstLine.startsWith("POST /users")) {
                handleUserRegistration(in, out);
                // POST für User Login
            } else if (firstLine.startsWith("POST /sessions")) {
                handleUserLogin(in, out);
            } else {
                sendResponse(out, 405, "Method Not Allowed", "Only POST requests are allowed.");
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

    private void handleUserRegistration(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;

        // Header lesen, um Content-Length zu bestimmen
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Den Body lesen
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            requestBody.append(bodyChars);
        }

        // JSON in User-Objekt umwandeln
        User user = objectMapper.readValue(requestBody.toString(), User.class);

        // Benutzer registrieren
        try {
            boolean registrationSuccessful = userLogic.registerUser(user.getUsername(), user.getPassword());
            if (registrationSuccessful) {
                sendResponse(out, 201, "Created", "{\"message\":\"User registered successfully.\"}");
            } else {
                sendResponse(out, 409, "Conflict", "{\"message\":\"User already exists.\"}");
            }
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    private void handleUserLogin(BufferedReader in, BufferedWriter out) throws IOException, SQLException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;

        // Header lesen, um Content-Length zu bestimmen
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Den Body lesen
        if (contentLength > 0) {
            char[] bodyChars = new char[contentLength];
            in.read(bodyChars, 0, contentLength);
            requestBody.append(bodyChars);
        }

        // JSON in User-Objekt umwandeln
        User user = objectMapper.readValue(requestBody.toString(), User.class);

        // Benutzer einloggen und Token generieren
        try {
            String token = userLogic.loginUser(user.getUsername(), user.getPassword());
            if (token != null) {
                String jsonResponse = "{\"token\":\"" + token + "\"}";
                sendResponse(out, 200, "OK", jsonResponse);
            } else {
                sendResponse(out, 401, "Unauthorized", "{\"message\":\"Invalid login credentials.\"}");
            }
        } catch (SQLException e) {
            sendResponse(out, 500, "Internal Server Error", "{\"message\":\"Database error: " + e.getMessage() + "\"}");
        }
    }

    // HTTP-Response senden
    private void sendResponse(BufferedWriter out, int statusCode, String statusMessage, String responseBody) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "\r\n" +
                responseBody;

        out.write(response);
        out.flush();
    }
}
