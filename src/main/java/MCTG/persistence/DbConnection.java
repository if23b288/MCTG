package MCTG.persistence;

import java.io.Closeable;
import java.sql.*;

public class DbConnection implements Closeable {
    private static DbConnection instance;

    private Connection connection;

    public DbConnection() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC driver not found");
            e.printStackTrace();
        }
    }

    public Connection connect(String database) throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + database, "user", "password");
    }

    public Connection connect() throws SQLException {
        return connect("mctg");
    }

    public Connection getConnection() {
        if(connection == null) {
            try {
                connection = DbConnection.getInstance().connect();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        return connection;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return getConnection().prepareStatement(sql, autoGeneratedKeys);
    }

    public boolean executeSql(String sql) throws SQLException {
        return executeSql(getConnection(), sql, false);
    }

    public static boolean executeSql(Connection connection, String sql, boolean ignoreIfFails) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            if(!ignoreIfFails)
                throw e;
            return false;
        }
    }

    @Override
    public void close() {
        if( connection!=null ) {
            try {
                connection.close();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
            connection = null;
        }
    }

    public static DbConnection getInstance() {
        if(instance == null)
            instance = new DbConnection();
        return instance;
    }

    public static void initDb() {
        // re-create the database
        try (Connection connection = getInstance().connect("")) {
            executeSql(connection, "DROP DATABASE mctg", true );
            executeSql(connection,  "CREATE DATABASE mctg", true );

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
                CREATE TABLE IF NOT EXISTS package (
                   pId SERIAL NOT NULL,
                   cId VARCHAR (255) NOT NULL,
                   PRIMARY KEY (pId, cId),
                   FOREIGN KEY (cId) REFERENCES card(cId)
                );
                CREATE TABLE IF NOT EXISTS stack (
                   username VARCHAR(255) NOT NULL,
                   cId VARCHAR(255) NOT NULL,
                   PRIMARY KEY (username, cId),
                   FOREIGN KEY (username) REFERENCES users(username),
                   FOREIGN KEY (cId) REFERENCES card(cId)
                );
                CREATE TABLE IF NOT EXISTS deck (
                    username VARCHAR(255) NOT NULL,
                    cId VARCHAR(255) NOT NULL,
                    PRIMARY KEY (username, cId),
                    FOREIGN KEY (username) REFERENCES users(username),
                    FOREIGN KEY (cId) REFERENCES card(cId)
                );
                CREATE TABLE IF NOT EXISTS profile (
                    username VARCHAR(255) PRIMARY KEY,
                    pName VARCHAR(255),
                    bio TEXT,
                    image VARCHAR(255),
                    FOREIGN KEY (username) REFERENCES users(username)
                );
                CREATE TABLE IF NOT EXISTS stats (
                    username VARCHAR(255) PRIMARY KEY,
                    wins INT NOT NULL DEFAULT 0,
                    losses INT NOT NULL DEFAULT 0,
                    draws INT NOT NULL DEFAULT 0,
                    elo INT NOT NULL DEFAULT 100,
                    FOREIGN KEY (username) REFERENCES users(username)
                );
                CREATE TABLE IF NOT EXISTS trade (
                    id VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255) NOT NULL,
                    cId VARCHAR(255) NOT NULL,
                    type VARCHAR(255) NOT NULL,
                    minDamage DOUBLE PRECISION NOT NULL,
                    FOREIGN KEY (username) REFERENCES users(username),
                    FOREIGN KEY (cId) REFERENCES card(cId)
                );
                """;
            executeSql(connection, sql, false);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
