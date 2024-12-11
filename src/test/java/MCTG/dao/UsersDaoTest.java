package MCTG.dao;

import MCTG.core.models.user.Users;
import MCTG.persistence.DbConnection;
import MCTG.persistence.dao.UsersDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsersDaoTest {
    static UsersDao usersDao;

    @BeforeEach
    public void setUp() throws Exception {
        usersDao = new UsersDao();
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE mctg", true );
            DbConnection.executeSql(connection,  "CREATE DATABASE mctg", true );
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
                """;
        try {
            DbConnection.getInstance().executeSql(sql);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    @Test
    public void testSaveAndGet() {
        String username = "testUser";
        Users user = new Users(username, "testPass", "", 10, new Timestamp(System.currentTimeMillis()));
        usersDao.save(user);

        Optional<Users> dbUser = usersDao.get(username);
        assertTrue(dbUser.isPresent());
        assertEquals(user.getUsername(), dbUser.get().getUsername());
        assertEquals(user.getPassword(), dbUser.get().getPassword());
        assertEquals(user.getToken(), dbUser.get().getToken());
        assertEquals(user.getCoins(), dbUser.get().getCoins());
        assertEquals(user.getLast_updated(), dbUser.get().getLast_updated());
    }

    @Test
    public void testSaveAndUpdateAndGet() {
        String username = "test";
        Users user = new Users(username, "testPass", "", 10, new Timestamp(System.currentTimeMillis()));
        usersDao.save(user);
        user.setToken("testToken-mctgToken");
        usersDao.update(user);
        Optional<Users> dbUser = usersDao.get(username);
        assertTrue(dbUser.isPresent());
        assertEquals(user.getUsername(), dbUser.get().getUsername());
        assertEquals(user.getPassword(), dbUser.get().getPassword());
        assertEquals(user.getToken(), dbUser.get().getToken());
        assertEquals(user.getCoins(), dbUser.get().getCoins());
        assertEquals(user.getLast_updated(), dbUser.get().getLast_updated());
    }
}
