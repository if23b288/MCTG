package MCTG.service;

import MCTG.core.models.user.Stats;
import MCTG.core.service.ScoreboardService;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.StatsDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ScoreboardServiceTest {
    static Dao<Stats> statsDao;
    static ScoreboardService scoreboardService;

    @BeforeAll
    public static void setUp() {
        statsDao = Mockito.mock(StatsDao.class);
        scoreboardService = new ScoreboardService(statsDao);
    }

    @Test
    public void testGetScoreboard() {
        Stats stats1 = new Stats("user1", 10, 2, 1, 100);
        Stats stats2 = new Stats("user2", 8, 3, 2, 90);
        Stats stats3 = new Stats("user3", 5, 5, 3, 80);

        when(statsDao.getAll()).thenReturn(List.of(stats1, stats2, stats3));

        String expectedScoreboard = """
                1. user1 elo: 100 w/l/d: 10/2/1
                2. user2 elo: 90 w/l/d: 8/3/2
                3. user3 elo: 80 w/l/d: 5/5/3
                """;

        String actualScoreboard = scoreboardService.getScoreboard();

        assertEquals(expectedScoreboard, actualScoreboard);
    }
}
