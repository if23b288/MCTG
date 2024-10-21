package MCTG.api.controller;

import MCTG.server.utils.Request;
import MCTG.server.utils.Response;
import lombok.Getter;
import com.fasterxml.jackson.databind.ObjectMapper;

@Getter
public abstract class Controller {
    private final ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
    }

    public abstract Response handleRequest(Request request);
}
