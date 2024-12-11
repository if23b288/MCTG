package MCTG.dao;

import MCTG.core.models.user.Profile;
import MCTG.persistence.DbConnection;
import MCTG.persistence.dao.ProfileDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProfileDaoTest {
    static ProfileDao profileDao;

    @BeforeEach
    public void setUp() throws Exception {
        profileDao = new ProfileDao();
        try (Connection connection = DbConnection.getInstance().connect("")) {
            DbConnection.executeSql(connection, "DROP DATABASE mctg", true );
            DbConnection.executeSql(connection,  "CREATE DATABASE mctg", true );
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        String sql = """
                CREATE TABLE IF NOT EXISTS profile (
                    username VARCHAR(255) PRIMARY KEY,
                    pName VARCHAR(255),
                    bio TEXT,
                    image VARCHAR(255),
                    FOREIGN KEY (username) REFERENCES users(username)
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
        var username = "testUser";
        var profile = new Profile("Test Name", "Test Bio", "Test Image");
        profile.setUsername(username);
        profileDao.save(profile);

        var result = profileDao.get(username);
        assertTrue(result.isPresent());
        assertEquals(profile.getUsername(), result.get().getUsername());
        assertEquals(profile.getPName(), result.get().getPName());
        assertEquals(profile.getBio(), result.get().getBio());
        assertEquals(profile.getImage(), result.get().getImage());
    }
}