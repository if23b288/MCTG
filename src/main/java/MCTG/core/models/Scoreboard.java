package MCTG.core.models;

import java.util.Map;

public class Scoreboard {
    private Map<Integer, Integer> scores;

    public Scoreboard(Map<Integer, Integer> scores) {
        this.scores = scores;
    }

    public void addScore(int playerId, int score) {
        this.scores.put(playerId, score);
    }
}
