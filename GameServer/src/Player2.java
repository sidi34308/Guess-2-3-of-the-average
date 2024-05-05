import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Player2 {
    public static void main(String[] args) {
        final String serverAddress = "localhost";
        final int serverPort = 13337;  

        try (Socket socket = new Socket(serverAddress, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.println("Connected to the server. Enter your commands:");

            Thread serverMessageReader = new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        System.out.println("Server: " + fromServer);
                    }
                } catch (IOException e) {
                    System.out.println("Connection to server lost.");
                }
            });
            serverMessageReader.start();

            String fromUser;
            while ((fromUser = stdIn.readLine()) != null) {
                out.println(fromUser); 
                if (fromUser.equalsIgnoreCase("exit")) {
                    break;  
                }
            }
        } catch (IOException e) {
            System.err.println("Could not connect to " + serverAddress + ":" + serverPort);
            e.printStackTrace();
        }
    }
}
