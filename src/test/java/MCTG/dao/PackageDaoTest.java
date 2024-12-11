package MCTG.dao;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.Package;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.monster.Monster;
import MCTG.core.models.cards.monster.MonsterCard;
import MCTG.persistence.DbConnection;
import MCTG.persistence.dao.PackageDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PackageDaoTest {
    static PackageDao packageDao;

    @BeforeEach
    public void setUp() throws Exception {
        packageDao = new PackageDao();
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE mctg", true);
            DbConnection.executeSql(connection, "CREATE DATABASE mctg", true);
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
                CREATE TABLE IF NOT EXISTS package (
                   pId SERIAL NOT NULL,
                   cId VARCHAR (255) NOT NULL,
                   PRIMARY KEY (pId, cId),
                   FOREIGN KEY (cId) REFERENCES card(cId)
                );
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
            DbConnection.getInstance().executeSql(cardSql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testSaveAndGet() throws SQLException {
        Card card1 = new MonsterCard("card1", "WaterGoblin", 30, Element.WATER, Monster.GOBLIN);
        Card card2 = new MonsterCard("card2", "Dragon", 50, Element.NORMAL, Monster.DRAGON);
        Card card3 = new SpellCard("card3", "FireSpell", 20, Element.FIRE);
        Card card4 = new MonsterCard("card4", "Ork", 35, Element.NORMAL, Monster.ORK);
        Package pack = new Package(1, List.of(card1, card2, card3, card4));
        packageDao.save(pack);

        Optional<Package> result = packageDao.get("1");
        assertTrue(result.isPresent());
        assertEquals(pack.getPId(), result.get().getPId());
        assertEquals(pack.getCards().size(), result.get().getCards().size());
        assertEquals(pack.getCards().get(0).getCId(), result.get().getCards().get(0).getCId());
        assertEquals(pack.getCards().get(1).getCId(), result.get().getCards().get(1).getCId());
    }

    @Test
    public void testSaveAndDelete() throws SQLException {
        Card card5 = new MonsterCard("card1", "WaterGoblin", 30, Element.WATER, Monster.GOBLIN);
        Card card6 = new MonsterCard("card2", "Dragon", 50, Element.NORMAL, Monster.DRAGON);
        Card card7 = new SpellCard("card3", "FireSpell", 20, Element.FIRE);
        Card card8 = new MonsterCard("card4", "Ork", 35, Element.NORMAL, Monster.ORK);
        Package pack = new Package(2, List.of(card5, card6, card7, card8));
        packageDao.save(pack);
        packageDao.delete("2");
        Optional<Package> result = packageDao.get("2");
        assertTrue(result.isPresent());
    }
}
