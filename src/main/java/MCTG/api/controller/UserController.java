package MCTG.api.controller;

import MCTG.core.models.Users;
import MCTG.persistence.dao.Dao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.SQLException;
import java.util.Collection;

public class UserController extends Controller {
    private final Dao<Users> userDao;

    public UserController(Dao<Users> userDao) {
        super();
        this.userDao = userDao;
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            return addUser(request);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ContentType.PLAIN_TEXT,
                ""
        );
    }

    // POST /users
    public Response addUser(Request request) {
        try {
            Collection<Users> users = userDao.getAll();
            Users user = this.getObjectMapper().readValue(request.getBody(), Users.class);
            if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername())))
            {
                return new Response(
                        HttpStatus.CONFLICT,
                        "Conflict",
                        ContentType.PLAIN_TEXT,
                        "User already exists"
                );
            }
            userDao.save(user);
            return new Response(
                    HttpStatus.CREATED,
                    "Created",
                    ContentType.JSON,
                    ""
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    ContentType.PLAIN_TEXT,
                    ""
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
