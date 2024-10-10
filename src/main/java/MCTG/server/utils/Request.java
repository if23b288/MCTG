package MCTG.server.utils;

import MCTG.server.http.Method;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Request {
    private Method method;
    private String path;
    private String pathname;
    private List<String> pathParts;
    private String params;
    private final HeaderMap headerMap = new HeaderMap();
    private String body;

    public void setPathname(String pathname) {
        this.pathname = pathname;
        String[] stringParts = pathname.split("/");
        this.pathParts = new ArrayList<>();
        for (String part :stringParts)
        {
            if (part != null && !part.isEmpty())
            {
                this.pathParts.add(part);
            }
        }
    }
}
