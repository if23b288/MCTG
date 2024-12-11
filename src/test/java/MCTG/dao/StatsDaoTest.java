package MCTG.dao;

import MCTG.core.models.user.Stats;
import MCTG.persistence.DbConnection;
import MCTG.persistence.dao.StatsDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatsDaoTest {
    static StatsDao statsDao;

    @BeforeEach
    public void setUp() throws Exception {
        statsDao = new StatsDao();
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE mctg", true );
            DbConnection.executeSql(connection,  "CREATE DATABASE mctg", true );
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        String sql = """
                CREATE TABLE IF NOT EXISTS stats (
                    username VARCHAR(255) PRIMARY KEY,
                    wins INT NOT NULL DEFAULT 0,
                    losses INT NOT NULL DEFAULT 0,
                    draws INT NOT NULL DEFAULT 0,
                    elo INT NOT NULL DEFAULT 100,
                    FOREIGN KEY (username) REFERENCES users(username)
                );
                """;
        try {
            DbConnection.getInstance().executeSql(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testSaveAndGet() throws SQLException {
        var username = "testUser";
        var stats = new Stats(username, 10, 5, 2, 105);
        statsDao.save(stats);

        var result = statsDao.get(username);
        assertTrue(result.isPresent());
        assertEquals(stats.getWins(), result.get().getWins());
        assertEquals(stats.getLosses(), result.get().getLosses());
        assertEquals(stats.getDraws(), result.get().getDraws());
        assertEquals(stats.getElo(), result.get().getElo());
    }

    @Test
    public void testSaveAndUpdateAndGet() throws Exception {
        var stats = new Stats("testUser", 10, 5, 2, 105);
        statsDao.save(stats);

        var updatedStats = new Stats("testUser", 15, 6, 3, 115);
        statsDao.update(updatedStats);

        var result = statsDao.get("testUser");
        assertTrue(result.isPresent());
        assertEquals(updatedStats.getWins(), result.get().getWins());
        assertEquals(updatedStats.getLosses(), result.get().getLosses());
        assertEquals(updatedStats.getDraws(), result.get().getDraws());
        assertEquals(updatedStats.getElo(), result.get().getElo());
    }
}