import java.util.*;

public class Game {
    private static int nextId = 1;
    private int id;
    private List<Player> players;
    private Map<Player, Integer> guesses = new HashMap<>();
    private boolean active = true;
    int roundNumber;
    int capacity;

    public Game() {
        id = nextId++;
        players = new ArrayList<>();
        active = false;
        roundNumber = 0;
        capacity = Server.getMaxPlayersPerGame();
    }

    public synchronized void removePlayer(Player player) {
        players.remove(player);
    }

    public boolean isFull() {
        return players.size() == capacity;
    }

    public synchronized int getId() {
        return id;
    }

    public synchronized boolean isActive() {
        return active;
    }

    public synchronized boolean addPlayer(Player player) {
        if (isFull()) {
            return false;
        }
        players.add(player);
        return true;
    }


    public synchronized void start() {
        active = true;
        roundNumber = 0;
        for (Player player : players) {
            player.resetPoints();
        }
        broadcastMessage("Game started! Round 1 begins.");
        playRounds();
        endGame();
    }
    
    
    public synchronized void makeMove(Player player, String move) {
        try {
            int guess = Integer.parseInt(move);
            guesses.put(player, guess);

            if (guesses.size() == players.size()) {
                processRound();
            }
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid move. Please enter a number.");
        }
    }
    
    private synchronized void processRound() {		//maybe remove
        roundNumber++;
        List<Integer> selections = new ArrayList<>(guesses.values());
        int average = calculateAverage(selections);
        List<Player> winners = findWinners(selections, average);
        announceRoundResults(selections, winners);
        eliminatePlayers();
        guesses.clear();

        if (players.size() > 1) {
            broadcastMessage("Round " + (roundNumber + 1) + " begins.");
        }else {
            endGame();
        }
        
    }

    private synchronized void playRounds() {
        while (active && players.size() > 1) {
            roundNumber++;
            List<Integer> selections = new ArrayList<>();
            for (Player player : players) {
                int selection = player.makeSelection();
                selections.add(selection);
            }
            int average = calculateAverage(selections);
            List<Player> winners = findWinners(selections, average);
            announceRoundResults(selections, winners);
            eliminatePlayers();
            if (players.size() == 1) {
                break;
            }
            broadcastMessage("Round " + (roundNumber + 1) + " begins.");
        }
    }

    private synchronized int calculateAverage(List<Integer> selections) {
        int sum = 0;
        for (int selection : selections) {
            sum += selection;
        }
        return sum / selections.size();
    }

    private synchronized List<Player> findWinners(List<Integer> selections, int average) {
        List<Player> winners = new ArrayList<>();
        double twoThirdsAverage = (double) average * 2 / 3;
        double minDifference = Double.MAX_VALUE;
        for (int i = 0; i < selections.size(); i++) {
            int selection = selections.get(i);
            double difference = Math.abs(twoThirdsAverage - selection);
            if (difference <= minDifference) {
                if (difference < minDifference) {
                    winners.clear();
                    minDifference = difference;
                }
                winners.add(players.get(i));
            }
        }
        return winners;
    }

    private synchronized void announceRoundResults(List<Integer> selections, List<Player> winners) {
        StringBuilder message = new StringBuilder();
        message.append("Round ").append(roundNumber).append(" results:\n");
        message.append("+---------+------------+-----------------+------------+-------+\n");
        message.append("| Round # | Selections | Average 2/3     | Winners    | Points|\n");
        message.append("+---------+------------+-----------------+------------+-------+\n");
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            int selection = selections.get(i);
            double twoThirdsAverage = (double) calculateAverage(selections) * 2 / 3;
            String result = winners.contains(player) ? "Winner" : "Loser";
            int points = player.getPoints();
            if (!winners.contains(player)) {
                player.subtractPoint();
            }
            message.append(String.format("| %-7d | %-10d | %-15.2f | %-10s | %-5d |\n",
                    roundNumber, selection, twoThirdsAverage, result, points));
        }
        message.append("+---------+------------+-----------------+------------+-------+");
        broadcastMessage(message.toString());
    }



    private synchronized void eliminatePlayers() {
        List<Player> eliminatedPlayers = new ArrayList<>();
        for (Player player : players) {
            if (player.getPoints() <= 0) {
                eliminatedPlayers.add(player);
            }
        }
        for (Player eliminatedPlayer : eliminatedPlayers) {
            guesses.remove(eliminatedPlayer);
            players.remove(eliminatedPlayer);
            broadcastMessage(eliminatedPlayer.getNickname() + " has been eliminated.");
        }
    }


    public synchronized List<Player> getPlayers() {
        return players;
    }

    private synchronized void endGame() {
        if (players.size() == 1) {
            Player winner = players.get(0);
            winner.winGame();  // Call winGame here
            broadcastMessage("Game over! " + winner.getNickname() + " wins!");
        }
        active = false;
    }

    private synchronized void broadcastMessage(String message) {
        for (Player player : players) {
            player.sendMessage(message);
        }
    }
}
