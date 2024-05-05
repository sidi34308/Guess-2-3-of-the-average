import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of players (2-6):");
        int numPlayers = scanner.nextInt();

        List<Player> players = new ArrayList<>();

        for (int i = 1; i <= numPlayers; i++) {
            Player player = new Player();
            player.setNickname("Player" + i);
            players.add(player);
        }

        Game game = new Game();
        for (Player player : players) {
            game.addPlayer(player);
        }

        game.start();
    }
}
