package MCTG.api.controller;

import MCTG.core.models.user.Users;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Timestamp;
import java.util.Optional;

public class SessionController extends Controller {
    private static final Logger LOGGER = LogManager.getLogger("SessionController");

    private final Dao<Users> userDao;

    public SessionController(Dao<Users> userDao) {
        super();
        this.userDao = userDao;
    }

    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {  // POST /sessions
            return login(request);
        }

        return new Response (
            HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                ""
        );
    }

    private Response login(Request request) {
        try {
            Users user = this.getObjectMapper().readValue(request.getBody(), Users.class);
            Optional<Users> dbUser = userDao.get(user.getUsername());
            if (dbUser.isEmpty() || !dbUser.get().getPassword().equals(user.getPassword()))
            {
                LOGGER.warn("Login failed for user: {}", user.getUsername());
                return new Response (
                        HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }

            String token = user.getUsername() + "-mtcgToken";
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            dbUser.get().setToken(token);
            dbUser.get().setLast_updated(timestamp);
            userDao.update(dbUser.get());

            return new Response (
                    HttpStatus.OK,
                    ContentType.PLAIN_TEXT,
                    token
            );
        } catch (JsonProcessingException e) {
            LOGGER.error("Error while parsing JSON", e);
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }
}
