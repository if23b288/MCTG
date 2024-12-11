package MCTG.service;

import MCTG.core.models.user.Users;
import MCTG.core.service.AuthorizationService;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.UsersDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class AuthorizationServiceTest {
    static Dao<Users> userDao;
    static AuthorizationService authorizationService;

    @BeforeAll
    public static void setUp() {
        userDao = Mockito.mock(UsersDao.class);
        authorizationService = new AuthorizationService(userDao);
    }

    @Test
    public void testIsAuthorizedWithValidToken() {
        String username = "testUser";
        String bearer = "Bearer testUser-token";
        Users user = new Users(username, "testPass", "testUser-token", 10, null);

        when(userDao.get(username)).thenReturn(Optional.of(user));

        assertTrue(authorizationService.isAuthorized(bearer));
    }

    @Test
    public void testIsAuthorizedWithInvalidToken() {
        String username = "testUser";
        String bearer = "Bearer invalid-token";
        Users user = new Users(username, "testPass", "testUser-token", 10, null);

        when(userDao.get(username)).thenReturn(Optional.of(user));

        assertFalse(authorizationService.isAuthorized(bearer));
    }

    @Test
    public void testIsAuthorizedWithValidUsernameAndToken() {
        String username = "testUser";
        String bearer = "Bearer testUser-token";
        Users user = new Users(username, "testPass", "testUser-token", 10, null);

        when(userDao.get(username)).thenReturn(Optional.of(user));

        assertTrue(authorizationService.isAuthorized(username, bearer));
    }

    @Test
    public void testIsAuthorizedWithInvalidUsernameAndToken() {
        String username = "testUser";
        String bearer = "Bearer invalid-token";
        Users user = new Users(username, "testPass", "testUser-token", 10, null);

        when(userDao.get(username)).thenReturn(Optional.of(user));

        assertFalse(authorizationService.isAuthorized(username, bearer));
    }
}
