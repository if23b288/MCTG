package MCTG.server.utils;

import java.util.HashMap;
import java.util.Map;

public class HeaderMap {
    private final Map<String, String> headers = new HashMap<>();

    public void ingest(String headerLine) {
        final String[] split = headerLine.split(":", 2);
        headers.put(split[0], split[1].trim());
    }

    public int getContentLength() {
        final String header = headers.get("Content-Length");
        if (header == null) {
            return 0;
        }
        return Integer.parseInt(header);
    }

    public String getAuthorization() {
        final String header = headers.get("Authorization");
        if (header == null) {
            return "";
        }
        return header;
    }
}
