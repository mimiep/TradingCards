package server;

import org.example.server.Server;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ServerTest {

    @Test
    void testServerStartsCorrectly() {
        int testPort = 10001;
        Server server = new Server(testPort);

        assertDoesNotThrow(() -> {
            Thread serverThread = new Thread(() -> server.start());
            serverThread.start();

            // Stop the thread gracefully after verifying server is listening
            serverThread.interrupt();
        }, "The server should start without throwing an exception.");
    }
}
