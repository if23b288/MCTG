package MCTG.controller;

import MCTG.api.controller.UserController;
import MCTG.core.models.user.Profile;
import MCTG.core.models.user.Users;
import MCTG.core.service.AuthorizationService;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.ProfileDao;
import MCTG.persistence.dao.UsersDao;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    static Dao<Users> userDao;
    static Dao<Profile> profileDao;
    static UserController userController;
    static AuthorizationService authorizationService;

    @BeforeAll
    public static void setUp() {
        authorizationService = Mockito.mock(AuthorizationService.class);
        userDao = Mockito.mock(UsersDao.class);
        profileDao = Mockito.mock(ProfileDao.class);
        userController = new UserController(userDao, profileDao, authorizationService);
    }

    @Test
    public void testAddUser() throws SQLException {
        String json = "{\"Username\":\"newUser\", \"Password\":\"newPass\"}";
        Request request = new Request();
        request.setBody(json);
        request.setMethod(Method.POST);
        request.setPathParts(List.of("users"));

        when(userDao.getAll()).thenReturn(List.of());
        doNothing().when(userDao).save(any(Users.class));

        Response response = userController.handleRequest(request);

        assertEquals(HttpStatus.CREATED.code, response.getStatus());
    }

    @Test
    public void testAddExistingUser() throws SQLException {
        String json = "{\"Username\":\"newUser\", \"Password\":\"newPass\"}";
        Request request = new Request();
        request.setBody(json);
        request.setMethod(Method.POST);
        request.setPathParts(List.of("users"));

        Users user = new Users("newUser", "password", "", 10, new Timestamp(System.currentTimeMillis()));
        when(userDao.getAll()).thenReturn(List.of(user));
        doNothing().when(userDao).save(any(Users.class));

        Response response = userController.handleRequest(request);

        assertEquals(HttpStatus.CONFLICT.code, response.getStatus());
        assertEquals("User already exists", response.getContent());
    }

    @Test
    public void testGetProfile() {
        String username = "testUser";
        Request request = new Request();
        request.setMethod(Method.GET);
        request.setPathParts(List.of("users", username));
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-mctgToken");

        Profile profile = new Profile("Name", "nothing to see", ":P");
        when(authorizationService.isAuthorized(any(), any())).thenReturn(true);
        when(profileDao.get(username)).thenReturn(Optional.of(profile));

        Response response = userController.handleRequest(request);

        assertEquals(HttpStatus.OK.code, response.getStatus());
        assertEquals(profile.toString(), response.getContent());
    }

    @Test
    public void testSaveProfile() throws SQLException {
        String username = "testUser";
        String json = "{\"Name\": \"Name\",  \"Bio\": \"nothing to see\", \"Image\": \":P\"}";
        Request request = new Request();
        request.setMethod(Method.PUT);
        request.setBody(json);
        request.setPathParts(List.of("users", username));
        request.getHeaderMap().ingest("Authorization: Bearer " + username + "-mctgToken");

        when(authorizationService.isAuthorized(any(), any())).thenReturn(true);
        doNothing().when(profileDao).save(any(Profile.class));

        Response response = userController.handleRequest(request);

        assertEquals(HttpStatus.CREATED.code, response.getStatus());
    }
}