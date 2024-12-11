package MCTG.dao;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.DbConnection;
import MCTG.persistence.dao.CardDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardDaoTest {
    static CardDao cardDao;

    @BeforeEach
    public void setUp() throws Exception {
        cardDao = new CardDao();
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE mctg", true );
            DbConnection.executeSql(connection,  "CREATE DATABASE mctg", true );
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        String sql = """
                CREATE TABLE IF NOT EXISTS card (
                   cId VARCHAR (255) PRIMARY KEY,
                   cardname VARCHAR (255) NOT NULL,
                   damage DOUBLE PRECISION NOT NULL,
                   elementType VARCHAR (255) NOT NULL,
                   monsterType VARCHAR (255) DEFAULT NULL
                );
                """;
        try {
            DbConnection.getInstance().executeSql(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testSaveAndGetMonsterCard() {
        String cardId = "testMonsterCard";
        Card card = new MonsterCard(cardId, "Test Monster", 50, Element.FIRE, Monster.DRAGON);
        cardDao.save(card);

        Optional<Card> result = cardDao.get(cardId);
        assertTrue(result.isPresent());
        assertEquals(MonsterCard.class, result.get().getClass());
        assertEquals(card.getCId(), result.get().getCId());
        assertEquals(card.getName(), result.get().getName());
        assertEquals(card.getDamage(), result.get().getDamage());
        assertEquals(card.getElementType(), result.get().getElementType());
        assertEquals(((MonsterCard) result.get()).getMonsterType(), ((MonsterCard) result.get()).getMonsterType());
    }

    @Test
    public void testSaveAndGetSpellCard() {
        String cardId = "testSpellCard";
        Card card = new SpellCard(cardId, "Test Spell", 30, Element.WATER);
        cardDao.save(card);

        Optional<Card> result = cardDao.get(cardId);
        assertTrue(result.isPresent());
        assertEquals(SpellCard.class, result.get().getClass());
        assertEquals(card.getCId(), result.get().getCId());
        assertEquals(card.getName(), result.get().getName());
        assertEquals(card.getDamage(), result.get().getDamage());
        assertEquals(card.getElementType(), result.get().getElementType());
    }
}
