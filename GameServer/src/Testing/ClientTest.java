//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//public class ClientTest {
//    private Client client;
//
//    @BeforeEach
//    public void setUp() {
//        client = new Client("localhost", 13337); // Ensure the server is running on localhost:13337 for this test.
//    }
//
//    @Test
//    public void testClientConnection() {
//        assertTrue(client.connect(), "Client should successfully connect to the server.");
//    }
//
//    @Test
//    public void testSendMessage() {
//        client.connect();
//        assertTrue(client.sendMessage("Hello Server!"), "Client should send messages to the server successfully.");
//    }
//
//    @Test
//    public void testReceiveMessage() {
//        client.connect();
//        client.sendMessage("Hello Server!");
//        String response = client.receiveMessage();
//        assertNotNull(response, "Client should receive a reply from the server.");
//    }
//
//    @Test
//    public void testClientDisconnection() {
//        client.connect();
//        client.disconnect();
//        assertFalse(client.isConnected(), "Client should be disconnected successfully.");
//    }
//}
