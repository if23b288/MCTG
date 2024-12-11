package MCTG.core.models.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Stats {
    private String username;
    private int wins;
    private int losses;
    private int draws;
    private int elo;

    public Stats() {
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
        this.elo = 100;
    }

    @Override
    public String toString() {
        return "Stats\n----------------------\nUsername: " + this.username + "\nElo: " + this.elo + "\nWins: " + this.wins + "\nLosses: " + this.losses + "\nDraws: " + this.draws + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Stats)) {
            return false;
        }
        Stats stats = (Stats) obj;
        return stats.wins == wins && stats.losses == losses && stats.draws == draws && stats.elo == elo;
    }

    @Override
    public int hashCode() {
        return wins + losses + draws + elo;
    }
}
