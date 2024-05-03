
import java.util.Scanner;

public class Player {
    private String nickname;
    private int points;
    private Game game;
    private Scanner scanner;

    public Player(String nickname) {
        this.nickname = nickname;
        this.points = 5;
        this.scanner = new Scanner(System.in);
    }

    public String getNickname() {
        return nickname;
    }

    public int getPoints() {
        return points;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public int makeSelection() {
        System.out.println("Enter your guess (between 0 and 100): ");
        int selection = scanner.nextInt();
        while (selection < 0 || selection > 100) {
            System.out.println("Invalid input. Please enter a number between 0 and 100: ");
            selection = scanner.nextInt();
        }
        return selection;
    }

    public void losePoint() {
        points--;
    }

    public void gainPoint() {
        points++;
    }
}
