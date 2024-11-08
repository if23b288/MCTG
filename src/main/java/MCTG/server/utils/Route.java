package MCTG.server.utils;

import MCTG.api.controller.Controller;
import lombok.Data;

@Data
public class Route {
    private final String route;
    private final Controller controller;
    private final Boolean needsAuthorization;
}
