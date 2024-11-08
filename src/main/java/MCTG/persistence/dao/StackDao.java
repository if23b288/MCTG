package MCTG.persistence.dao;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Stack;
import MCTG.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class StackDao implements Dao<Stack> {
    @Override
    public Optional<Stack> get(String username) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT card.cId, card.cardname, card.damage, card.elementType, card.monsterType
                FROM stack
                JOIN card ON stack.cId = card.cId
                WHERE stack.username = ?
                """)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            List<Card> cards = CardDao.getCardsList(resultSet);
            return Optional.of(new Stack(username, cards));
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Collection<Stack> getAll() {
        /*List<Stack> stacks = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT stack.username, card.cId, card.cardname, card.damage, card.elementType, card.monsterType
                FROM stack
                JOIN card ON stack.cId = card.cId
                ORDER BY stack.username
                """)
        ) {
            var resultSet = statement.executeQuery();
            String username = "";
            while (resultSet.next()) {
                if (resultSet.getString(1).equals(username)) {
                    if (!username.isEmpty()) {
                        stacks.add(new Stack(username, cards));
                    }
                    username = resultSet.getString(1);
                    cards = new ArrayList<>();
                }
                if (resultSet.getString(6).isEmpty()) {
                    cards.add(new SpellCard(
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getInt(4),
                            Element.valueOf(resultSet.getString(5))
                    ));
                } else {
                    cards.add(new MonsterCard(
                            resultSet.getString(2),
                            resultSet.getString(3),
                            resultSet.getInt(4),
                            Element.valueOf(resultSet.getString(5)),
                            Monster.valueOf(resultSet.getString(6))
                    ));
                }
            }
            stacks.add(new Stack(username, cards));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return stacks;*/


        return null;
    }

    @Override
    public void save(Stack stack) {
        String sql = "INSERT INTO stack (username, cId) VALUES (?, ?)";
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement(sql)) {
            for (Card card : stack.getCards()) {
                statement.setString(1, stack.getUsername());
                statement.setString(2, card.getCId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Stack stack) {

    }

    @Override
    public void delete(Stack stack) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                DELETE FROM stack
                WHERE cId = ?;
                """)
        ) {
            for (Card card : stack.getCards()) {
                statement.setString(1, card.getCId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
