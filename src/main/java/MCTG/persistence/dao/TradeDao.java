package MCTG.persistence.dao;

import MCTG.core.models.Trade;
import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class TradeDao implements Dao<Trade> {
    @Override
    public Optional<Trade> get(String id) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT id, username, c.cId, c.cardname, c.damage, c.elementType, c.monsterType, type, minDamage
                FROM trade
                JOIN card c on trade.cId = c.cId
                WHERE id = ?
                """)
        ) {
            statement.setString(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(new Trade(
                        resultSet.getString("id"),
                        resultSet.getString("username"),
                        getCard(resultSet),
                        resultSet.getString("type"),
                        resultSet.getDouble("minDamage")
                ));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Trade> getAll() {
        Collection<Trade> allTrades = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT id, username, c.cId, c.cardname, c.damage, c.elementType, c.monsterType, type, minDamage
                FROM trade
                JOIN card c on trade.cId = c.cId
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                allTrades.add(new Trade(
                        resultSet.getString("id"),
                        resultSet.getString("username"),
                        getCard(resultSet),
                        resultSet.getString("type"),
                        resultSet.getDouble("minDamage")
                ));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return allTrades;
    }

    private Card getCard(ResultSet resultSet) throws SQLException {
        if (resultSet.getString(7).isEmpty()) {
            return new SpellCard(
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getInt(5),
                    Element.valueOf(resultSet.getString(6))
            );
        } else {
            return new MonsterCard(
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getInt(5),
                    Element.valueOf(resultSet.getString(6)),
                    Monster.valueOf(resultSet.getString(7))
            );
        }
    }

    @Override
    public void save(Trade trade) throws SQLException {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO trade (id, username, cId, type, minDamage)
                VALUES (?, ?, ?, ?, ?)
                """)
        ) {
            statement.setString(1, trade.getId());
            statement.setString(2, trade.getUsername());
            statement.setString(3, trade.getCardToTrade().getCId());
            statement.setString(4, trade.getType());
            statement.setDouble(5, trade.getMinimumDamage());
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Trade stats) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                UPDATE stats
                SET wins = ?, losses = ?, draws = ?, elo = ?
                WHERE username = ?
                """)
        ) {

            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void delete(String id) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                DELETE FROM trade
                WHERE id = ?
                """)
        ) {
            statement.setString(1, id);
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
