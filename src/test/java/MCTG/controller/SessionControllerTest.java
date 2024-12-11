package MCTG.controller;

import MCTG.api.controller.SessionController;
import MCTG.core.models.user.Users;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.UsersDao;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class SessionControllerTest {
    static Dao<Users> userDao;
    static SessionController sessionController;

    @BeforeAll
    public static void setUp() {
        userDao = Mockito.mock(UsersDao.class);
        sessionController = new SessionController(userDao);
    }

    @Test
    public void testLoginSuccess() {
        String json = "{\"Username\":\"testUser\", \"Password\":\"testPass\"}";
        Request request = new Request();
        request.setBody(json);
        request.setMethod(Method.POST);

        when(userDao.get("testUser")).thenReturn(Optional.of(new Users("testUser", "testPass", "", 10, new Timestamp(System.currentTimeMillis()))));
        doNothing().when(userDao).update(any(Users.class));

        Response response = sessionController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals("testUser-mtcgToken", response.getContent());
    }

    @Test
    public void testLoginFailure() {
        String json = "{\"Username\":\"testUser\", \"Password\":\"wrongPass\"}";
        Request request = new Request();
        request.setBody(json);
        request.setMethod(Method.POST);

        Users user = new Users("testUser", "testPass", "", 10, new Timestamp(System.currentTimeMillis()));
        when(userDao.get("testUser")).thenReturn(Optional.of(user));

        Response response = sessionController.handleRequest(request);

        assertEquals(HttpStatus.UNAUTHORIZED.code, response.getStatus());
        assertEquals("Login failed", response.getContent());
    }
}