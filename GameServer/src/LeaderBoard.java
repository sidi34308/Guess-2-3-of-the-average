
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeaderBoard {
    private List<PlayerScore> scores; 

    public LeaderBoard() {
        scores = new ArrayList<>();
    }

    public synchronized void updateScore(String playerNickname, int points) {
        PlayerScore playerScore = findPlayerScore(playerNickname);
        if (playerScore != null) {
            playerScore.setScore(playerScore.getScore() + points);
        } else {
            scores.add(new PlayerScore(playerNickname, points));
        }
        Collections.sort(scores, Collections.reverseOrder());
    }

    public synchronized int getScore(String playerNickname) {
        PlayerScore playerScore = findPlayerScore(playerNickname);
        return (playerScore != null) ? playerScore.getScore() : 0;
    }

    public synchronized List<String> getTopPlayers(int n) {
        List<String> topPlayers = new ArrayList<>();
        for (int i = 0; i < Math.min(n, scores.size()); i++) {
            topPlayers.add(scores.get(i).getPlayerNickname());
        }
        return topPlayers;
    }

    private PlayerScore findPlayerScore(String playerNickname) {
        for (PlayerScore score : scores) {
            if (score.getPlayerNickname().equals(playerNickname)) {
                return score;
            }
        }
        return null;
    }

    private static class PlayerScore implements Comparable<PlayerScore> {
        private String playerNickname;
        private int score;

        public PlayerScore(String playerNickname, int score) {
            this.playerNickname = playerNickname;
            this.score = score;
        }

        public String getPlayerNickname() {
            return playerNickname;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        @Override
        public int compareTo(PlayerScore other) {
            return Integer.compare(this.score, other.score);
        }
    }
}
