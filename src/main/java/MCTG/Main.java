package MCTG;

import MCTG.server.HttpServer;
import MCTG.server.utils.Router;

public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer(10001, new Router());
        server.start();
    }
}