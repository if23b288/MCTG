package MCTG.core.service;

import MCTG.core.models.user.Stats;
import MCTG.persistence.dao.Dao;

import java.util.Collection;

public class ScoreboardService {
    private final Dao<Stats> statsDao;

    public ScoreboardService(Dao<Stats> statsDao) {
        this.statsDao = statsDao;
    }

    public String getScoreboard() {
        StringBuilder scoreboard = new StringBuilder();
        Collection<Stats> allStats = statsDao.getAll();
        Stats statsBefore = null;
        int place = 1;
        for (Stats stats : allStats) {
            if (statsBefore != null) {
                if (stats.equals(statsBefore)) {
                    place--;
                }
            }
            scoreboard.append(place).append(". ").append(stats.getUsername()).append(" elo: ").append(stats.getElo()).append(" w/l/d: ").append(stats.getWins()).append("/").append(stats.getLosses()).append("/").append(stats.getDraws()).append("\n");
            place++;
            statsBefore = stats;
        }
        return scoreboard.toString();
    }
}
