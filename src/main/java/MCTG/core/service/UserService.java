package MCTG.core.service;

import MCTG.api.controller.UserController;
import MCTG.persistence.Database;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;

public class UserService implements Service {
    private final UserController userController;

    public UserService() {
        userController = new UserController();
    }

    @Override
    public Response handleRequest(Request request, Database database) {
        if (request.getMethod() == Method.POST) {
            return userController.addUser(request, database);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ContentType.PLAIN_TEXT,
                ""
        );
    }
}
