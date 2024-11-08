package MCTG.server.http;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "CREATED"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT FOUND"),
    CONFLICT(409, "CONFLICT"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    public final int code;
    public final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
