package MCTG.persistence.dao;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.monster.*;
import MCTG.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class CardDao implements Dao<Card> {
    @Override
    public Optional<Card> get(String cId) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT cId, cardname, damage, elementType, monsterType
                FROM card
                WHERE cId = ?
                """)
        ) {
            statement.setString(1, cId);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                if (resultSet.getString(5).isEmpty()) {
                    return Optional.of(new SpellCard (
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getInt(3),
                            Element.valueOf(resultSet.getString(4))
                    ));
                }
                return Optional.of(new MonsterCard(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        Element.valueOf(resultSet.getString(4)),
                        Monster.valueOf(resultSet.getString(5))
                ));
            }
        } catch(SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Card> getAll() {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT cId, cardname, damage, elementType, monsterType
                FROM card
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            return getCardsList(resultSet);
        } catch (SQLException exception) {
            exception.printStackTrace();
            return new ArrayList<>();
        }

    }

    @Override
    public void save(Card card) {
        String monsterType = "";
        if (card.getClass() == MonsterCard.class)
        {
            monsterType = ((MonsterCard) card).getMonsterType().toString();
        }
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO card
                (cId, cardname, damage, elementType, monsterType)
                VALUES (?, ?, ?, ?, ?);
                """)
        ) {
            statement.setString(1, card.getCId());
            statement.setString(2, card.getName());
            statement.setDouble(3, card.getDamage());
            statement.setString(4, card.getElementType().toString());
            statement.setString(5, monsterType);
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Card card) { }

    @Override
    public void delete(String id) {

    }

    static List<Card> getCardsList(ResultSet resultSet) {
        List<Card> cards = new ArrayList<>();
        try {
            while (resultSet.next()) {
                if (resultSet.getString(5).isEmpty()) {
                    cards.add(new SpellCard(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getInt(3),
                            Element.valueOf(resultSet.getString(4))
                    ));
                } else {
                    cards.add(new MonsterCard(
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getInt(3),
                            Element.valueOf(resultSet.getString(4)),
                            Monster.valueOf(resultSet.getString(5))
                    ));
                }
            }
        }
        catch (SQLException exception) {
            exception.printStackTrace();
        }
        return cards;
    }
}
