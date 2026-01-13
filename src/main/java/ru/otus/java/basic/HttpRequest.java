package ru.otus.java.basic;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String rawRequest;
    private HttpMethod method;
    private String uri;
    private Map<String, String> params;
    private String body;
    private Long path;

    public Long getPath() {
        return path;
    }

    public String getUri() {
        return uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public String getBody() {
        return body;
    }

    public boolean containsParameter(String name) {
        return params.containsKey(name);
    }

    public String getRoutingKey() {
        return method + " " + uri;
    }

    public HttpRequest(String rawRequest) {
        this.rawRequest = rawRequest;
        this.params = new HashMap<>();
        this.path = null;
        parse();
    }

    private void parse() {
        int left = rawRequest.indexOf(" ");
        int right = rawRequest.indexOf(" ", left + 1);
        method = HttpMethod.valueOf(rawRequest.substring(0, left));
        uri = rawRequest.substring(left + 1, right);
        if (uri.contains("?")) {
            String[] tokens = uri.split("[?]");
            uri = tokens[0];
            String[] keysAndValues = tokens[1].split("[&]");
            for (String o : keysAndValues) {
                String[] keyValue = o.split("[=]");
                params.put(keyValue[0], keyValue[1]);
            }
        }
        if (method == HttpMethod.POST || method == HttpMethod.PUT) {
            body = rawRequest.substring(rawRequest.indexOf("\r\n\r\n") + 4);
        }
        if (method == HttpMethod.DELETE) {
            String[] parts = uri.split("/");
            this.path = Long.parseLong(parts[parts.length - 1]);
            int index = uri.lastIndexOf("/");
            uri = uri.substring(0, index);
        }
        if (method == HttpMethod.GET) {
            String[] parts = uri.split("/");
            if (!("items".equals(parts[parts.length - 1])) && "items".equals(parts[parts.length - 2])) {
                this.path = Long.parseLong(parts[parts.length - 1]);
                int index = uri.lastIndexOf("/");
                uri = uri.substring(0, index);
            }
        }
    }

    public void info(boolean showRawRequest) {
        System.out.println("METHOD: " + method);
        System.out.println("URI: " + uri);
        System.out.println("PARAMS: " + params);
        System.out.println("BODY: " + body);
        if (showRawRequest) {
            System.out.println(rawRequest);
        }
    }
}
