package MCTG.api.controller;

import MCTG.core.models.user.Users;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.Timestamp;
import java.util.Optional;

public class SessionController extends Controller {
    private final Dao<Users> userDao;

    public SessionController(Dao<Users> userDao) {
        super();
        this.userDao = userDao;
    }

    public Response handleRequest(Request request) {
        // POST /sessions
        if (request.getMethod() == Method.POST) {
            return login(request);
        }

        return new Response (
            HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                ""
        );
    }

    // POST /sessions
    private Response login(Request request) {
        try {
            Users user = this.getObjectMapper().readValue(request.getBody(), Users.class);
            Optional<Users> dbUser = userDao.get(user.getUsername());
            if (dbUser.isEmpty() || !dbUser.get().getPassword().equals(user.getPassword()))
            {
                return new Response (
                        HttpStatus.UNAUTHORIZED,
                        ContentType.PLAIN_TEXT,
                        "Login failed"
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
            e.printStackTrace();
            return new Response (
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }
}
