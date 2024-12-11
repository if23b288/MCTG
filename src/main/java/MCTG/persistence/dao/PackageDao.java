package MCTG.persistence.dao;

import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Element;
import MCTG.core.models.cards.Package;
import MCTG.core.models.cards.SpellCard;
import MCTG.core.models.cards.monster.*;
import MCTG.persistence.DbConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class PackageDao implements Dao<Package> {
    @Override
    public Optional<Package> get(String pId) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT c.cId, c.cardname, c.damage, c.elementType, c.monsterType
                FROM package
                JOIN card c on package.cId = c.cId
                WHERE pId = ?
                """)
        ) {
            statement.setInt(1, Integer.parseInt(pId));
            ResultSet resultSet = statement.executeQuery();
            List<Card> cards = CardDao.getCardsList(resultSet);
            return Optional.of(new Package(Integer.parseInt(pId), cards));
        } catch (SQLException exception) {
            exception.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Collection<Package> getAll() {
        List<Package> packages = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT pId, c.cId, c.cardname, c.damage, c.elementType, c.monsterType
                FROM package
                JOIN card c on package.cId = c.cId
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();

            List<Card> cards = new ArrayList<>();
            int pId = -1;
            while (resultSet.next()) {
                if (pId != resultSet.getInt(1)) {
                    if (pId != -1) {
                        packages.add(new Package(pId, cards));
                    }
                    cards = new ArrayList<>();
                    pId = resultSet.getInt(1);
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
            if (pId != -1) {
                packages.add(new Package(pId, cards));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return packages;
    }

    @Override
    public void save(Package p) throws SQLException {
        String insertFirstCardSql = "INSERT INTO package (cId) VALUES (?)";
        String insertAdditionalCardsSql = "INSERT INTO package (pId, cId) VALUES (?, ?)";
        try (PreparedStatement firstCardStatement = DbConnection.getInstance().prepareStatement(insertFirstCardSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement additionalCardsStatement = DbConnection.getInstance().prepareStatement(insertAdditionalCardsSql)) {
            DbConnection.getInstance().getConnection().setAutoCommit(false);
            firstCardStatement.setString(1, p.getCards().getFirst().getCId());
            firstCardStatement.executeUpdate();

            ResultSet generatedKeys = firstCardStatement.getGeneratedKeys();
            int newPackageId;
            if (generatedKeys.next()) {
                newPackageId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Failed to create package, no ID obtained.");
            }

            for (int i = 1; i < p.getCards().size(); i++) {
                additionalCardsStatement.setInt(1, newPackageId);
                additionalCardsStatement.setString(2, p.getCards().get(i).getCId());
                additionalCardsStatement.addBatch();
            }
            additionalCardsStatement.executeBatch();
            DbConnection.getInstance().getConnection().commit();
        }  catch (SQLException e) {
                DbConnection.getInstance().getConnection().rollback();
        } finally {
            DbConnection.getInstance().getConnection().setAutoCommit(true);
        }
    }

    @Override
    public void update(Package p) {

    }

    @Override
    public void delete(String id) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                DELETE FROM package WHERE pId = ?
                """)
        ) {
            statement.setInt(1, Integer.parseInt(id));
            statement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
