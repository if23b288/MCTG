package MCTG.api.controller;

import MCTG.core.models.user.Profile;
import MCTG.core.models.user.Users;
import MCTG.core.service.AuthorizationService;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class UserController extends Controller {
    private final Dao<Users> userDao;
    private final Dao<Profile> profileDao;
    private final AuthorizationService authorizationService;

    public UserController(Dao<Users> userDao, Dao<Profile> profileDao) {
        super();
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.authorizationService = new AuthorizationService(userDao);
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getPathParts().size() > 1) {
            String username = request.getPathParts().get(1);
            String token = request.getHeaderMap().getAuthorization();
            if (!authorizationService.isAuthorized(username, token))
            {
                return new Response (
                        HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
            if (request.getMethod() == Method.PUT) {
                return saveProfile(username, request);
            } else if (request.getMethod() == Method.GET) {
                return getProfile(username, request);
            }
        }

        if (request.getMethod() == Method.POST) {
            return addUser(request);
        }

        return new Response (
                HttpStatus.BAD_REQUEST,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    // GET /users/{username}
    private Response getProfile(String username, Request request) {
        Optional<Profile> profile = profileDao.get(username);
        return new Response(
                HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                profile.get().toString()
        );
    }

    // PUT /users/{username}
    private Response saveProfile(String username, Request request) {
        try {
            Profile profile = this.getObjectMapper().readValue(request.getBody(), Profile.class);
            profile.setUsername(username);
            profileDao.save(profile);
            return new Response (
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // POST /users
    public Response addUser(Request request) {
        try {
            Collection<Users> users = userDao.getAll();
            Users user = this.getObjectMapper().readValue(request.getBody(), Users.class);
            if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername())))
            {
                return new Response (
                        HttpStatus.CONFLICT,
                        ContentType.PLAIN_TEXT,
                        "User already exists"
                );
            }
            userDao.save(user);
            return new Response (
                    HttpStatus.CREATED,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
