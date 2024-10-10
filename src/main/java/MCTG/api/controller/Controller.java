package MCTG.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

@Getter
public abstract class Controller {
    private final ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
    }
}
