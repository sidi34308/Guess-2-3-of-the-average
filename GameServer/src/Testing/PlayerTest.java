import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class PlayerTest {
    @Test
    public void testAddPoints() {
        Player player = new Player();
        player.setPoints(5);
        player.addPoints(3);
        assertEquals(8, player.getPoints(), "Player should have 8 points after adding 3.");
    }
}
