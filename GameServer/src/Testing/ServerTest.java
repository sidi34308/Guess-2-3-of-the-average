import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class ServerTest {
    private Server server;
    private Thread serverThread;
    private final int testPort = 13337;  // Ensure this port is free to use for testing

    @BeforeEach
    public void setUp() {
        // Initialize the server and run it on a separate thread to not block the test thread.
        server = new Server(testPort);
        serverThread = new Thread(() -> server.start());
        serverThread.start();
    }

    @Test
    public void testServerAcceptsConnections() {
        try (Socket testClientSocket = new Socket("localhost", testPort)) {
            assertTrue(testClientSocket.isConnected(), "Server should accept connections.");
        } catch (IOException e) {
            fail("Should not throw an exception when connecting to server");
        }
    }

    @Test
    public void testServerShutdown() {
        server.stop();
        try {
            serverThread.join(1000);  // Wait for the server thread to terminate
            assertFalse(serverThread.isAlive(), "Server thread should terminate upon shutdown.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Server shutdown was interrupted");
        }
    }

    @AfterEach
    public void tearDown() {
        if (server != null && server.isRunning()) {
            server.stop();
        }
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
    }
}
