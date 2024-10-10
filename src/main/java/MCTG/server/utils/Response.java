package MCTG.server.utils;

import MCTG.server.http.ContentType;
import MCTG.server.http.HttpStatus;

public class Response {
    private int status;
    private String message;
    private String contentType;
    private String content;

    public Response(HttpStatus status, String message, ContentType contentType, String content) {
        this.status = status.code;
        this.message = message;
        this.contentType = contentType.type;
        this.content = content;
    }

    public String get() {
        return "HTTP/1.1 " + this.status + " " + this.message + "\r\n" +
                "Cache-Control: max-age=0\r\n" +
                "Connection: close\r\n" +
                "Content-Type: " + this.contentType + "\r\n" +
                "Content-Length: " + this.content.length() + "\r\n" +
                "\r\n" +
                this.content;
    }
}
