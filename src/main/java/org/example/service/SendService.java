package org.example.service;

import java.io.BufferedWriter;
import java.io.IOException;

//Für Request, Send betreffend zuständig
public class SendService {
    // HTTP-Response senden
    public void sendResponse(BufferedWriter out, int statusCode, String statusMessage, String responseBody) throws IOException {
        String response = "HTTP/1.1 " + statusCode + " " + statusMessage + "\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "\r\n" +
                responseBody + "\r\n";

        out.write(response);
        out.flush();
    }
}
