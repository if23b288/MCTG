package MCTG.persistence.dao;

import MCTG.core.models.Users;
import MCTG.persistence.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class UsersDaoDb implements Dao<Users> {
    /**
     * initializes the database with its tables
     */
    // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
    public static void initDb() {
        // re-create the database
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE mctg", true );
            DbConnection.executeSql(connection,  "CREATE DATABASE mctg", true );
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        // create the table
        // PostgreSQL documentation: https://www.postgresqltutorial.com/postgresql-create-table/
        try {
            DbConnection.getInstance().executeSql("""
                CREATE TABLE IF NOT EXISTS users (
                    id serial PRIMARY KEY,
                    username VARCHAR ( 255 ) NOT NULL,
                    password VARCHAR (255 ) NOT NULL,
                    token VARCHAR ( 255 ) NOT NULL,
                    coins INT NOT NULL DEFAULT 20,
                    elo INT NOT NULL DEFAULT 100,
                    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Optional<Users> get(int id) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT id, username, password, token, coins, elo, last_updated
                FROM users
                WHERE id=?
                """)
        ) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(new Users (
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getInt(5),
                        resultSet.getInt(6),
                        resultSet.getTimestamp(7)
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Users> get(String text) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT id, username, password, token, coins, elo, last_updated
                FROM users
                WHERE username=?
                """)
        ) {
            statement.setString(1, text);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return Optional.of(new Users (
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getInt(5),
                        resultSet.getInt(6),
                        resultSet.getTimestamp(7)
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Collection<Users> getAll() {
        ArrayList<Users> result = new ArrayList<>();
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                SELECT id, username, password, token, coins, elo, last_updated
                FROM users
                """)
        ) {
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
                result.add( new Users(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getInt(5),
                        resultSet.getInt(6),
                        resultSet.getTimestamp(7)
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(Users user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                INSERT INTO users
                (username, password, token, coins, elo, last_updated)
                VALUES (?, ?, ?, ?, ?, ?);
                """)
        ) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getToken());
            statement.setInt(4, user.getCoins());
            statement.setInt(5, user.getElo());
            statement.setTimestamp(6, user.getLast_updated());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void update(Users updatedUser) {
        // persist the updated item
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                UPDATE users
                SET username = ?, password = ?, token = ?, coins = ?, elo = ?, last_updated = ?
                WHERE id = ?;
                """)
        ) {
            statement.setString(1, updatedUser.getUsername());
            statement.setString(2, updatedUser.getPassword());
            statement.setString(3, updatedUser.getToken());
            statement.setInt(4, updatedUser.getCoins());
            statement.setInt(5, updatedUser.getElo());
            statement.setTimestamp(6, updatedUser.getLast_updated());
            statement.setInt(7, updatedUser.getId());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void delete(Users user) {
        try (PreparedStatement statement = DbConnection.getInstance().prepareStatement("""
                DELETE FROM users
                WHERE id = ?;
                """)
        ) {
            statement.setInt(1, user.getId());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
