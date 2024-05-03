
import java.util.ArrayList;
import java.util.List;

public class Game {
    private static int nextId = 1;
    private static final int MAX_PLAYERS = 6;

    private int id;
    private List<Player> players;
    private boolean active;
    private int roundNumber;

    public Game() {
        this.id = nextId++;
        this.players = new ArrayList<>();
        this.active = false;
        this.roundNumber = 0;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void addPlayer(Player player) {
        if (players.size() < MAX_PLAYERS) {
            players.add(player);
            player.setGame(this);
        } else {
            System.out.println("Game is already full");
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        player.setGame(null);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void startGame() {
        active = true;
        announce("Game started! Waiting for players to make their selections...");
        playRound();
    }

    private void playRound() {
        roundNumber++;
        int[] selections = new int[players.size()];
        StringBuilder message = new StringBuilder();
        message.append("Round ").append(roundNumber).append(": Players, please select a number between 0 and 100.\n");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int selection = player.makeSelection();
            selections[i] = selection;
            message.append(player.getNickname()).append(": ").append(selection).append("\n");
        }
        announce(message.toString());

        // Calculate average
        int sum = 0;
        for (int selection : selections) {
            sum += selection;
        }
        double average = sum / (double) selections.length;
        int twoThirdsAverage = (int) (2 * average) / 3;
        announce("Two-thirds of the average: " + twoThirdsAverage);

        List<Player> winners = findWinners(selections, twoThirdsAverage);
        announce("Winning number for this round is " + twoThirdsAverage);
        if (winners.size() > 0) {
            StringBuilder winnerMessage = new StringBuilder("Winners of this round are: ");
            for (Player winner : winners) {
                winner.gainPoint();
                winnerMessage.append(winner.getNickname()).append(", ");
            }
            winnerMessage.delete(winnerMessage.length() - 2, winnerMessage.length());
            announce(winnerMessage.toString());
        } else {
            announce("No winners this round");
        }

        announce("Round " + roundNumber + " ended!");
        announceGameStatus();
        if (!isGameOver()) {
            playRound();
        } else {
            endGame();
        }
    }

    private List<Player> findWinners(int[] selections, int winningNumber) {
        List<Player> winners = new ArrayList<>();
        int minDifference = Integer.MAX_VALUE;
        for (int i = 0; i < selections.length; i++) {
            int difference = Math.abs(selections[i] - winningNumber);
            if (difference < minDifference) {
                minDifference = difference;
                winners.clear();
                winners.add(players.get(i));
            } else if (difference == minDifference) {
                winners.add(players.get(i));
            }
        }
        return winners;
    }

    private boolean isGameOver() {
        return players.size() < 2 || roundNumber >= 10;
    }

    public void endGame() {
        active = false;
        Player winner = null;
        for (Player player : players) {
            if (player.getPoints() > 0) {
                winner = player;
                break;
            }
        }
        if (winner != null) {
            announce("Game over! " + winner.getNickname() + " wins!");
        } else {
            announce("Game over! No winner this time.");
        }
    }

    private void announce(String message) {
        System.out.println(message);
    }

    private void announceGameStatus() {
        StringBuilder status = new StringBuilder("Game Status:\n");
        for (Player player : players) {
            status.append(player.getNickname()).append(": ").append(player.getPoints()).append(" points\n");
        }
        announce(status.toString());
    }
}
