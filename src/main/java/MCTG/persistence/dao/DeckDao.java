package MCTG.persistence.dao;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Deck;
import MCTG.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class DeckDao implements Dao<Deck> {
    @Override
    public Optional<Deck> get(String username) {
        Deck deck = new Deck();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT c.cId, c.cardname, c.damage, c.elementType, c.monsterType
                FROM deck
                JOIN card c on deck.cId = c.cId
                WHERE username = ?
                """)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            List<Card> cards = CardDao.getCardsList(resultSet);
            deck.setUsername(username);
            deck.setCards(cards);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.of(deck);
    }

    @Override
    public Collection<Deck> getAll() {
        return List.of();
    }

    @Override
    public void save(Deck deck) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO deck (username, cId)
                VALUES (?, ?)
                """)
        ) {
            for (Card card : deck.getCards()) {
                statement.setString(1, deck.getUsername());
                statement.setString(2, card.getCId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Deck deck) {

    }

    @Override
    public void delete(String id) {

    }
}
