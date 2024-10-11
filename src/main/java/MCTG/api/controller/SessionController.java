package MCTG.api.controller;

import MCTG.core.models.User;
import MCTG.persistence.Database;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import com.fasterxml.jackson.core.JsonProcessingException;

public class SessionController extends Controller {
    public SessionController() {
        super();
    }

    // POST /sessions
    public Response login(Request request, Database database) {
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            User dbUser = database.getUser(user.getUsername());
            if (dbUser == null || !dbUser.getPassword().equals(user.getPassword()))
            {
                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }

            String token = user.getUsername() + "-mtcgToken";

            database.addToken(token, dbUser.getUsername());

            return new Response(
                    HttpStatus.OK,
                    "OK",
                    ContentType.PLAIN_TEXT,
                    token
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
