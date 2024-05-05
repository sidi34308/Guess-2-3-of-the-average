import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.io.IOException;


	
public class Server {
	private static final int PORT = 13337;
    private static Set<Player> players = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private static LeaderBoard leaderBoard = new LeaderBoard();
    private static final int MAX_PLAYERS_PER_GAME = 6;
    private long lastHeartbeatTime;
    private ServerSocket serverSocket;
    private boolean running = false;
    private final int port;

    public Server(int port) {
        this.port = port;
    }
	
	public static void main(String[] args) {
		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("The server is now listening on port: " + PORT);
			
			while(true) {
				Socket socket =serverSocket.accept();
				System.out.println("A new client has connected!");
				
				new ClientHandler(socket).start();
			}
		}	catch (Exception e) {
			System.err.println("Server exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	 public void start() {
	        running = true;
	        try {
	            serverSocket = new ServerSocket(port);
	            System.out.println("Server started on port " + port);

	            while (running) {
	                try {
	                    Socket clientSocket = serverSocket.accept();
	                    handleClient(clientSocket);
	                } catch (IOException e) {
	                    if (!running) {
	                        System.out.println("Server Stopped.");
	                        return;
	                    }
	                    throw new RuntimeException("Error accepting client connection", e);
	                }
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("Cannot open port " + port, e);
	        }
	    }
	 
	 public void stop() {
	        running = false;
	        try {
	            if (serverSocket != null) {
	                serverSocket.close();
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("Error closing server", e);
	        }
	    }
	 
	 public boolean isRunning() {
	        return running;
	    }
	 
	  private void handleClient(Socket clientSocket) {
	        System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
	    }
	
	
	public static void broadcastChat(String message) {
        for (Player player : players) {
            player.sendChatMessage(message);
        }
    }
	
	public void updateHeartbeat() {
	    lastHeartbeatTime = System.currentTimeMillis();
	}

	public long getLastHeartbeatTime() {
	    return lastHeartbeatTime;
	}

	public synchronized void removePlayer(Player player) {
        player.leaveGame();  // Implement this method
        broadcastMessage(player.getNickname() + " has been disconnected due to inactivity.");
        players.remove(player);
    }
    
    public static void updateLeaderboard(String playerName) {
        leaderBoard.updateScore(playerName, 1); // Increment the score by 1 for each win
    }

    public static String getLeaderboard() {
        List<String> topPlayers = leaderBoard.getTopPlayers(5); // Adjust this number as needed
        StringBuilder leaderboardDisplay = new StringBuilder("Leaderboard:\n");
        int rank = 1;
        for (String player : topPlayers) {
            leaderboardDisplay.append(rank++)
                .append(". ")
                .append(player)
                .append(" - ")
                .append(leaderBoard.getScore(player))
                .append(" wins\n");
        }
        return leaderboardDisplay.toString();
    }
    
    public static int getMaxPlayersPerGame() {
        return MAX_PLAYERS_PER_GAME;
    }
    
    public void monitorHeartbeats() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for (Player player : players) {
                    if (now - player.getLastInteractionTime() > 10000) {
                        removePlayer(player);
                    }
                }
            }
        }, 0, 15000);
    }
    
    public void broadcastMessage(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }

    
    

}

	