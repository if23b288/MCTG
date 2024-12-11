package MCTG.persistence.dao;

import MCTG.core.models.user.Stats;
import MCTG.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class StatsDao implements Dao<Stats> {
    @Override
    public Optional<Stats> get(String username) {
        Stats stats = new Stats();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT username, wins, losses, draws, elo
                FROM stats
                WHERE username = ?
                """)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                stats.setUsername(resultSet.getString("username"));
                stats.setWins(resultSet.getInt("wins"));
                stats.setLosses(resultSet.getInt("losses"));
                stats.setDraws(resultSet.getInt("draws"));
                stats.setElo(resultSet.getInt("elo"));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.of(stats);
    }

    @Override
    public Collection<Stats> getAll() {
        Collection<Stats> allStats = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT username, wins, losses, draws, elo
                FROM stats
                ORDER BY elo DESC, wins DESC, losses ASC, draws DESC
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Stats stats = new Stats(
                        resultSet.getString("username"),
                        resultSet.getInt("wins"),
                        resultSet.getInt("losses"),
                        resultSet.getInt("draws"),
                        resultSet.getInt("elo")
                );
                allStats.add(stats);
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return allStats;
    }

    @Override
    public void save(Stats stats) throws SQLException {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO stats (username, wins, losses, draws, elo)
                VALUES (?, ?, ?, ?, ?)
                """)
        ) {
            statement.setString(1, stats.getUsername());
            statement.setInt(2, stats.getWins());
            statement.setInt(3, stats.getLosses());
            statement.setInt(4, stats.getDraws());
            statement.setInt(5, stats.getElo());

            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Stats stats) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                UPDATE stats
                SET wins = ?, losses = ?, draws = ?, elo = ?
                WHERE username = ?
                """)
        ) {
            statement.setInt(1, stats.getWins());
            statement.setInt(2, stats.getLosses());
            statement.setInt(3, stats.getDraws());
            statement.setInt(4, stats.getElo());
            statement.setString(5, stats.getUsername());
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void delete(String username) {

    }
}
