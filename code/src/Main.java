
public class Main {
    public static void main(String[] args) {
        // Create a game
        Game game = new Game();

        // Create a player
        Player player = new Player("Player1");

        // Add the player to the game
        game.addPlayer(player);

        // Start the game
        game.startGame();
    }
}
