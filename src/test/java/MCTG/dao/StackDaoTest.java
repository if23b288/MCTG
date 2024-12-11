package MCTG.dao;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.Stack;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.DbConnection;
import MCTG.persistence.dao.StackDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StackDaoTest {
    static StackDao stackDao;

    @BeforeEach
    public void setUp() throws Exception {
        stackDao = new StackDao();
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE mctg", true);
            DbConnection.executeSql(connection, "CREATE DATABASE mctg", true);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                   username VARCHAR (255) PRIMARY KEY,
                   password VARCHAR (255) NOT NULL,
                   token VARCHAR (255) NOT NULL,
                   coins INT NOT NULL DEFAULT 20,
                   elo INT NOT NULL DEFAULT 100,
                   last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                );
                CREATE TABLE IF NOT EXISTS card (
                   cId VARCHAR (255) PRIMARY KEY,
                   cardname VARCHAR (255) NOT NULL,
                   damage DOUBLE PRECISION NOT NULL,
                   elementType VARCHAR (255) NOT NULL,
                   monsterType VARCHAR (255) DEFAULT NULL
                );
                CREATE TABLE IF NOT EXISTS stack (
                    username VARCHAR(255) NOT NULL,
                    cId VARCHAR(255) NOT NULL,
                    PRIMARY KEY (username, cId),
                    FOREIGN KEY (username) REFERENCES users(username),
                    FOREIGN KEY (cId) REFERENCES card(cId)
                );
                """;
        String userSql = """
                INSERT INTO users (username, password, token, coins, elo)
                VALUES ('user1', 'password', 'token', 20, 100);
                """;
        String cardSql = """
                INSERT INTO card (cId, cardname, damage, elementType, monsterType)
                VALUES ('card1', 'WaterGoblin', 30.0, 'Water', 'Goblin');
                INSERT INTO card (cId, cardname, damage, elementType, monsterType)
                VALUES ('card2', 'Dragon', 50.0, 'Normal', 'Dragon');
                INSERT INTO card (cId, cardname, damage, elementType, monsterType)
                VALUES ('card3', 'FireSpell', 20.0, 'Fire', '');
                INSERT INTO card (cId, cardname, damage, elementType, monsterType)
                VALUES ('card4', 'Ork', 35.0, 'Normal', 'Ork');
                """;
        try {
            DbConnection.getInstance().executeSql(sql);
            DbConnection.getInstance().executeSql(userSql);
            DbConnection.getInstance().executeSql(cardSql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testSaveAndGet() {
        String username = "user1";
        Card card1 = new MonsterCard("card1", "WaterGoblin", 30, Element.WATER, Monster.GOBLIN);
        Card card2 = new MonsterCard("card2", "Dragon", 50, Element.NORMAL, Monster.DRAGON);
        Card card3 = new SpellCard("card3", "FireSpell", 20, Element.FIRE);
        Card card4 = new MonsterCard("card4", "Ork", 35, Element.NORMAL, Monster.ORK);
        Stack stack = new Stack(username, List.of(card1, card2, card3, card4));
        stackDao.save(stack);

        Optional<Stack> result = stackDao.get(username);
        assertTrue(result.isPresent());
        assertEquals(stack.getUsername(), result.get().getUsername());
        assertEquals(stack.getCards().size(), result.get().getCards().size());
        assertEquals(stack.getCards().get(0).getCId(), result.get().getCards().get(0).getCId());
        assertEquals(stack.getCards().get(1).getCId(), result.get().getCards().get(1).getCId());
    }
}
