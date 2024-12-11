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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class UserController extends Controller {
    private static final Logger LOGGER = LogManager.getLogger("UserController");

    private final Dao<Users> userDao;
    private final Dao<Profile> profileDao;
    private final AuthorizationService authorizationService;

    public UserController(Dao<Users> userDao, Dao<Profile> profileDao, AuthorizationService authorizationService) {
        super();
        this.userDao = userDao;
        this.profileDao = profileDao;
        this.authorizationService = authorizationService;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getPathParts().size() > 1) {
            String username = request.getPathParts().get(1);
            String token = request.getHeaderMap().getAuthorization();
            if (!authorizationService.isAuthorized(username, token))
            {
                LOGGER.warn("Unauthorized access");
                return new Response (
                        HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
            if (request.getMethod() == Method.PUT) {  // PUT /users/{username}
                return saveProfile(username, request);
            } else if (request.getMethod() == Method.GET) {  // GET /users/{username}
                return getProfile(username, request);
            }
        }

        if (request.getMethod() == Method.POST) {  // POST /users
            return addUser(request);
        }
        LOGGER.warn("Invalid method");
        return new Response (
                HttpStatus.BAD_REQUEST,
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    private Response getProfile(String username, Request request) {
        Optional<Profile> profile = profileDao.get(username);
        return new Response(
                HttpStatus.OK,
                ContentType.PLAIN_TEXT,
                profile.get().toString()
        );
    }

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
        } catch (JsonProcessingException | SQLException e) {
            LOGGER.error("Error parsing JSON", e);
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }

    // POST /users
    public Response addUser(Request request) {
        try {
            Collection<Users> users = userDao.getAll();
            Users user = this.getObjectMapper().readValue(request.getBody(), Users.class);
            if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername())))
            {
                LOGGER.warn("User already exists");
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
        } catch (JsonProcessingException | SQLException e) {
            LOGGER.error("Error parsing JSON", e);
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }
}
