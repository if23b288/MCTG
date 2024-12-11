package MCTG.dao;

import MCTG.core.models.Trade;
import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.DbConnection;
import MCTG.persistence.dao.TradeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TradeDaoTest {
    static TradeDao tradeDao;

    @BeforeEach
    public void setUp() throws Exception {
        tradeDao = new TradeDao();
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
                CREATE TABLE IF NOT EXISTS trade (
                    id VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    cId VARCHAR(255) NOT NULL,
                    type VARCHAR(255) NOT NULL,
                    minDamage DOUBLE NOT NULL,
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
    public void testSaveAndGet() throws SQLException {
        Card card = new MonsterCard("card1", "WaterGoblin", 30, Element.WATER, Monster.GOBLIN);
        Trade trade = new Trade("trade1", "user1", card, "spell", 25.0);
        tradeDao.save(trade);

        Optional<Trade> result = tradeDao.get("trade1");
        assertTrue(result.isPresent());
        assertEquals(trade.getId(), result.get().getId());
        assertEquals(trade.getUsername(), result.get().getUsername());
        assertEquals(trade.getCardToTrade().getCId(), result.get().getCardToTrade().getCId());
        assertEquals(trade.getType(), result.get().getType());
        assertEquals(trade.getMinimumDamage(), result.get().getMinimumDamage());
    }
}
