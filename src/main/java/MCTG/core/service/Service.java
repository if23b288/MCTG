package MCTG.core.service;

import MCTG.persistence.Database;
import MCTG.server.utils.Request;
import MCTG.server.utils.Response;

public interface Service {
    Response handleRequest(Request request, Database database);
}
