import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final Logger logger = Logger.getLogger(Client.class.getName());
 
	private String serverIp;
    private int serverPort;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;

    public static void main(String[] args) {
        Client client = new Client("localhost", 13337); // Adjust the IP and port as needed
        Thread readerThread = new Thread(client::receiveMessages);
        readerThread.start();
        client.readInput();
    }
    
    

    public Client(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
        try {
            socket = new Socket(serverIp, serverPort);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            startHeartbeat();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error connecting to server: " + e.getMessage(), e);
        }
    }


    public void sendMessage(String message) {
        output.println(message);
        logger.log(Level.INFO, "Sent message: " + message);
    }

    public void readInput() {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String userInput = scanner.nextLine();
                sendMessage(userInput);
                if (userInput.equalsIgnoreCase("exit")) {
                    closeConnection();
                    break;
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error reading user input: " + e.getMessage(), e);
        }
    }

    public void receiveMessages() {
        try {
            String serverResponse;
            while ((serverResponse = input.readLine()) != null) {
                logger.log(Level.INFO, "Received message: " + serverResponse);
                System.out.println("Server: " + serverResponse);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading server response: " + e.getMessage(), e);
        }
    }
    
    public void startHeartbeat() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                sendMessage("HEARTBEAT");
            }
        }, 0, 15000);  // Sends heartbeat every 15 seconds
    }


    public void closeConnection() {
        try {
            if (socket != null) socket.close();
            if (input != null) input.close();
            if (output != null) output.close();
            logger.log(Level.INFO, "Connection closed successfully");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing client connection: " + e.getMessage(), e);
        }
    }
}
