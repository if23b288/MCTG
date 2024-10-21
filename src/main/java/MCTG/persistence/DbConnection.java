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
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return connection;
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return getConnection().prepareStatement(sql);
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

    public static boolean executeSql(Connection connection, String sql) throws SQLException {
        return executeSql(connection, sql, false);
    }

    @Override
    public void close() {
        if( connection!=null ) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            connection = null;
        }
    }

    public static DbConnection getInstance() {
        if(instance == null)
            instance = new DbConnection();
        return instance;
    }
}
