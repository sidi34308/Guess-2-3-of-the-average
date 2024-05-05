import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Player extends Thread {
    private Server server;
	private String nickname;
	private Game currentGame;
	private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int points;
    private boolean isActive;
    private Scanner inputScanner;
    private long lastInteractionTime;


    
    public Player() {
        inputScanner = new Scanner(System.in);
    }
	
	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void addPoints(int points) {
        this.points += points;
    }
	
	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public void updateLastInteractionTime() {
        lastInteractionTime = System.currentTimeMillis();
    }

    public long getLastInteractionTime() {
        return lastInteractionTime;
    }
	
	public Player(Socket socket) {
    	this.socket = socket;
    	this.points = 5;
//        this.server = server;
    	this.isActive = true;
    	
    	try {
    		out = new PrintWriter(socket.getOutputStream(), true);
    		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	} catch (IOException e) {
    		System.err.println("Error setting up streams: " + e.getMessage());
    		e.printStackTrace();
    		close();
    		isActive = false;		//stops the thread if fails
    	}
    }
    
	public void run() {
        try {
            String inputLine;
            while (isActive && (inputLine = in.readLine()) != null) {
                processMessage(inputLine);
            }
        } catch (IOException e) {
            System.err.println("Error in Player run: " + e.getMessage());
        } finally {
            close();
        }
    }
	
	 private void listenToServer() {
	        new Thread(() -> {
	            try {
	                String fromServer;
	                while ((fromServer = in.readLine()) != null) {
	                    System.out.println("Server: " + fromServer);
	                    if ("exit".equalsIgnoreCase(fromServer)) {
	                        break;
	                    }
	                }
	            } catch (IOException e) {
	                System.out.println("Lost connection to server.");
	            }
	        }).start();
	    }
	 
	 
	 public void start() {
	        listenToServer();
	    }
	
	public void sendMessage(String message) {
	      
        updateLastInteractionTime();
        System.out.println(message);
	}
    
	
	public void processMessage(String message) {
        if (message.startsWith("CHAT")) {
            String chatMessage = message.substring(5);
            out.println("CHAT " + nickname + ": " + chatMessage);
        } else if (message.startsWith("JOIN")) {
            joinGame(message.substring(5));
        } else if (message.equals("LEAVE")) {
            leaveGame();
        } else if (message.startsWith("MOVE")) {
            if (currentGame != null) {
                String move = message.substring(5);
                currentGame.makeMove(this, move);
            }
        } else if (message.equals("LEADERBOARD")) {
            showLeaderboard();
        } else {
            System.out.println("Unknown command");
        }
    }
    
	
	
	
	  private void joinGame(String gameName) {
	        out.println("JOIN " + gameName);
	    }

	  
	    public void leaveGame() {
	        out.println("LEAVE");

	    }

	    private void showLeaderboard() {
	        out.println("LEADERBOARD");
	    }
    
    public void winGame() {
        this.addPoints(1);  

        Server.updateLeaderboard(nickname);

        this.sendMessage("Congratulations, " + nickname + "! You have won this round!");
    }


    public int makeSelection() {
        System.out.println(nickname + ", please enter your selection (0-100):");
        while (true) {
            String input = inputScanner.nextLine();
            try {
                int selection = Integer.parseInt(input);
                if (selection >= 0 && selection <= 100) {
                    return selection;
                } else {
                    System.out.println("Invalid selection. Please enter a number between 0 and 100.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
    
    public void resetPoints() {
        this.points = 5; // The initial points 
    }
    
    public void sendChatMessage(String message) {
        sendMessage("[CHAT] " + message);
    }
    
    public void subtractPoint() {
		this.points=points-1;
	}

    private void close() {
        try {
            if (socket != null) socket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.err.println("Error closing player connection: " + e.getMessage());
        } finally {
            isActive = false;
            server.removePlayer(this); // Remove from list
        }
    }


}
