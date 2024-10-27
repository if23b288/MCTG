package MCTG.server;

import MCTG.persistence.DbConnection;
import MCTG.server.utils.Router;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final Router router;
    private final int port;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public HttpServer(int port, Router router) {
        this.port = port;
        this.router = router;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.print("Server listening on port " + port + "\n");
            while (true) {
                DbConnection.initDb();
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new RequestHandler(clientSocket, this.router));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}