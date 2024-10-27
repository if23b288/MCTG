package MCTG.server.utils;

import MCTG.api.controller.CardController;
import MCTG.api.controller.PackageController;
import MCTG.api.controller.SessionController;
import MCTG.api.controller.UserController;
import MCTG.core.models.Users;
import MCTG.core.models.cards.Card;
import MCTG.core.models.cards.Package;
import MCTG.core.service.AuthorizationService;
import MCTG.persistence.dao.CardDao;
import MCTG.persistence.dao.Dao;
import MCTG.persistence.dao.PackageDao;
import MCTG.persistence.dao.UsersDao;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;

public class Router {
    private final UserController userController;
    private final SessionController sessionController;
    private final PackageController packageController;
    private final CardController cardController;

    private final AuthorizationService authorizationService;

    public Router() {
        Dao<Users> userDao = new UsersDao();
        Dao<Package> packageDao = new PackageDao();
        Dao<Card> cardDao = new CardDao();
        userController = new UserController(userDao);
        sessionController = new SessionController(userDao);
        cardController = new CardController(cardDao);
        packageController = new PackageController(packageDao, cardController);
        authorizationService = new AuthorizationService(userDao);
    }

    public Response resolve(Request request) {
        switch (request.getPathname()) {
            case "/users" -> {
                return userController.handleRequest(request);
            }
            case "/sessions" -> {
                return sessionController.handleRequest(request);
            }
            case "/packages" -> {
                if (authorizationService.isAuthorized(request.getHeaderMap().getAuthorization())) {
                    cardController.handleRequest(request);
                    return packageController.handleRequest(request);
                }

                return new Response(
                        HttpStatus.UNAUTHORIZED,
                        "Unauthorized",
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
            default -> {
                return new Response(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        ContentType.PLAIN_TEXT,
                        ""
                );
            }
        }
    }
}
