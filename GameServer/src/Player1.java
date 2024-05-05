import java.util.Scanner;

public class Player1 {
    private Game game;  

    public Player1() {
        this.game = new Game();
    }

    public void connectToServer() {
        System.out.println("Connected to server. Type 'JOIN' to start the game.");
        handleCommands();
    }

    private void handleCommands() {
        Scanner scanner = new Scanner(System.in);
        String input;
        
        while (true) {
            input = scanner.nextLine();  // Read user input

            if (input.equalsIgnoreCase("JOIN")) {
                System.out.println("Joining game...");
                startGame();
                break; 
            }
        }

        scanner.close();
    }

    private void startGame() {
        this.game.start();
    }

    public static void main(String[] args) {
        Player1 player = new Player1();
        player.connectToServer();
    }
}
