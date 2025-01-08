package service;

import org.example.service.SendService;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendServiceTest {


    @Test
    void testSendResponse() throws IOException {
        CharArrayWriter charArrayWriter = new CharArrayWriter();
        BufferedWriter writer = new BufferedWriter(charArrayWriter);

        // Instanz des SendService
        SendService sendService = new SendService();

        // Die zu sendende Antwort
        int statusCode = 200;
        String statusMessage = "OK";
        String responseBody = "{\"message\":\"Success\"}";

        // Die Methode aufrufen
        sendService.sendResponse(writer, statusCode, statusMessage, responseBody);

        // Erwartete Antwort
        String expectedResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "Content-Length: 21\r\n" +
                "\r\n" +
                "{\"message\":\"Success\"}\r\n";

        assertEquals(expectedResponse, charArrayWriter.toString());
    }



}
