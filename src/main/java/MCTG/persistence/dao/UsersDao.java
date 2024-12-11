package MCTG.persistence.dao;

import MCTG.core.models.user.Users;
import MCTG.persistence.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class UsersDao implements Dao<Users> {
    @Override
    public Optional<Users> get(String username) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT username, password, token, coins, last_updated
                FROM users
                WHERE username=?
                """)
        ) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(new Users (
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getTimestamp(5)
                ));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Users> getAll() {
        ArrayList<Users> result = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT username, password, token, coins, last_updated
                FROM users
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                result.add( new Users(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getTimestamp(5)
                ));
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(Users user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO users
                (username, password, token, coins, last_updated)
                VALUES (?, ?, ?, ?, ?);
                """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.setInt(4, user.getCoins());
            statement.setTimestamp(5, user.getLast_updated());
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void update(Users updatedUser) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                UPDATE users
                SET password = ?, token = ?, coins = ?, last_updated = ?
                WHERE username = ?;
                """)
        ) {
            statement.setString(1, updatedUser.getPassword());
            statement.setString(2, updatedUser.getToken());
            statement.setInt(3, updatedUser.getCoins());
            statement.setTimestamp(4, updatedUser.getLast_updated());
            statement.setString(5, updatedUser.getUsername());
            statement.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void delete(String username) {

    }
}
