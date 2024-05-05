import java.io.*;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

            String clientMessage;

            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Received: " + clientMessage);
            }
        } catch (IOException e) {
            System.err.println("IO Exception in client handler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Cannot close socket: " + e.getMessage());
            }
        }
    }
}
