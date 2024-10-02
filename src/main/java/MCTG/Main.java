package MCTG;

import MCTG.server.HttpServer;

public class Main {
    public static void main(String[] args) {
        HttpServer server = new HttpServer();
        server.start(8080);
    }
}