package MCTG.core.service;

import MCTG.api.controller.SessionController;
import MCTG.persistence.Database;
import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;
import MCTG.server.http.Method;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;

public class SessionService implements Service {
    private final SessionController sessionController;

    public SessionService() {
        sessionController = new SessionController();
    }

    @Override
    public Response handleRequest(Request request, Database database) {
        if (request.getMethod() == Method.POST) {
            return sessionController.login(request, database);
        }

        return new Response(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                ContentType.JSON,
                "[]"
        );
    }
}
