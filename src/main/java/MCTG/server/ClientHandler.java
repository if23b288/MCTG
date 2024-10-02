package MCTG.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        // Anfrage lesen, Request verarbeiten und Antwort zurücksenden
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            StringBuilder builder = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null && !inputLine.isEmpty()){
                builder.append(inputLine).append(System.lineSeparator());
            }
            // TODO process properly

            String response = processRequest(builder.toString());
            out.print(response);
            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String processRequest(String request) {
        // Basic request parsing (only GET method for simplicity)
        if (request.startsWith("GET")) {
            return "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n" + getJsonResponse();
        } else {
            return "HTTP/1.1 405 Method Not Allowed\r\n\r\n";
        }
    }

    private String getJsonResponse() {
        // Example response object
        ResponseObject responseObject = new ResponseObject("Hello, World!");
        try {
            return objectMapper.writeValueAsString(responseObject);
        } catch (IOException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    static class ResponseObject {
        public String message;

        public ResponseObject(String message) {
            this.message = message;
        }
    }
}