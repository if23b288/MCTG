package MCTG.api.controller;

import MCTG.core.models.User;
import MCTG.persistence.Database;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class UserController extends Controller {
    public UserController() {
        super();
    }

    // POST /users
    public Response addUser(Request request, Database database) {
        try {
            List<User> users = database.getUsers();
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            if (users.stream().anyMatch(u -> u.getUsername().equals(user.getUsername())))
            {
                return new Response(
                        HttpStatus.CONFLICT,
                        "Conflict",
                        ContentType.PLAIN_TEXT,
                        "User already exists"
                );
            }
            database.addUser(user);
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
        }

    }
}
