package MCTG.server;

import java.io.*;
import java.net.Socket;

import MCTG.persistence.Database;
import MCTG.server.utils.Request;
import MCTG.server.utils.RequestBuilder;
import MCTG.server.utils.Response;
import MCTG.server.utils.Router;

public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private final Router router;
    private BufferedReader in;
    private BufferedWriter out;
    private final Database database;

    public RequestHandler(Socket clientSocket, Router router, Database database) {
        this.clientSocket = clientSocket;
        this.router = router;
        this.database = database;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            Request request = new RequestBuilder().buildRequest(in);
            Response response = this.router.resolve(request.getPathname()).handleRequest(request, database);

            out.write(response.get());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}