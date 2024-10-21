package MCTG.server.utils;

import MCTG.api.controller.SessionController;
import MCTG.api.controller.UserController;
import MCTG.core.models.Users;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.UsersDaoDb;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;

public class Router {
    private final Dao<Users> userDao;
    private final UserController userController;
    private final SessionController sessionController;

    public Router() {
        userDao = new UsersDaoDb();
        userController = new UserController(userDao);
        sessionController = new SessionController(userDao);
    }

    public Response resolve(Request request) {
        if (request.getPathname().equals("/users")) {
            return userController.handleRequest(request);
        } else if (request.getPathname().equals("/sessions")) {
            return sessionController.handleRequest(request);
        } else {
            return new Response(
                    HttpStatus.NOT_FOUND,
                    "Not Found",
                    ContentType.PLAIN_TEXT,
                    ""
            );
        }
    }
}
