package MCTG.server.utils;

import MCTG.core.service.Service;

import java.util.*;

public class Router {
    private Map<String, Service> serviceRegistry = new HashMap<>();

    public void addService(String route, Service service) {
        this.serviceRegistry.put(route, service);
    }

    public Service resolve(String route) {
        return this.serviceRegistry.get(route);
    }
}
