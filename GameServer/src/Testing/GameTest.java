import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameTest {
    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    void testAddPlayer() {
        Player player1 = new Player(); 
        Player player2 = new Player();
        assertTrue(game.addPlayer(player1), "Player 1 should be added successfully");
        assertTrue(game.addPlayer(player2), "Player 2 should be added successfully");

        // Fill up the game
        for (int i = 0; i < game.capacity - 2; i++) {
            assertTrue(game.addPlayer(new Player()), "Additional player should be added");
        }

        // Test adding a player to a full game
        assertFalse(game.addPlayer(new Player()), "No more players should be added if the game is full");
    }

    @Test
    void testGameStartAndRoundProcessing() {
        // Adding minimum players to start the game
        game.addPlayer(new Player());
        game.addPlayer(new Player());

        // Start the game
        game.start();
        assertTrue(game.isActive(), "Game should be active after starting");

        // Assuming makeMove processes input correctly
        game.makeMove(game.getPlayers().get(0), "50");
        game.makeMove(game.getPlayers().get(1), "30");

        // Verify round advancement or processing
        assertEquals(1, game.roundNumber, "Round number should advance after all players make a move");
    }

    @Test
    void testGameEnd() {
        // Setup and start game
        game.addPlayer(new Player());
        game.addPlayer(new Player());
        game.start();

        // Simulate game ending
        while (game.getPlayers().size() > 1) {
            game.getPlayers().forEach(player -> game.makeMove(player, "100")); // Making sure the game processes rounds
        }

        assertFalse(game.isActive(), "Game should not be active after ending");
    }
}
