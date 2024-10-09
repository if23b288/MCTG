package MCTG;

import MCTG.core.service.SessionService;
import MCTG.core.service.UserService;
import MCTG.server.HttpServer;
import MCTG.server.utils.Router;

public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer(10001, configureRouter());
        server.start();
    }

    public static Router configureRouter() {
        Router router = new Router();
        router.addService("/users", new UserService());
        router.addService("/sessions", new SessionService());
        return router;
    }
}