package org.example.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;


public class RequestHandler implements Runnable {
    private final Socket socket;

    public RequestHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             OutputStream out = socket.getOutputStream()) {

            String requestLine = in.readLine(); // erste Zeile der Anfrage lesen
            System.out.println("Request: " + requestLine);

            // Beispielhafte Verarbeitung: nur GET-Anfragen unterstützen
            if (requestLine != null && requestLine.startsWith("GET")) {
                handleGetRequest(out);
            } else if (requestLine != null && requestLine.startsWith("POST")) {
                handlePostRequest(out);
            } else {
                sendResponse(out, 405, "Method Not Allowed", "Only GET and POST are allowed.");
            }

        } catch (IOException e) {
            System.out.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private void handleGetRequest(OutputStream out) throws IOException {
        String responseBody = "Hello, this is a response to a GET request!";
        sendResponse(out, 200, "OK", responseBody);
    }

    private void handlePostRequest(OutputStream out) throws IOException {
        // Hier kannst du die Logik für POST-Anfragen implementieren.
        String responseBody = "This is a response to a POST request!";
        sendResponse(out, 200, "OK", responseBody);
    }

    private void sendResponse(OutputStream out, int statusCode, String statusMessage, String responseBody) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "\r\n" +
                responseBody;

        out.write(response.getBytes());
        out.flush();
    }
}
