package ru.otus.java.basic;

import ru.otus.java.basic.application.CreateItemsProcessor;
import ru.otus.java.basic.application.DeleteItemsProccessor;
import ru.otus.java.basic.application.GetItemsProcessor;
import ru.otus.java.basic.application.UpdateItemsProcessor;
import ru.otus.java.basic.config.Config;
import ru.otus.java.basic.exceptions_handling.BadRequestException;
import ru.otus.java.basic.processors.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Dispatcher {
    private Map<HttpMethod, RequestProcessor> routes;
    private final String uri = "/shop/api/v1/items";
    private RequestProcessor defaultNotFoundProcessor;
    private RequestProcessor defaultStaticResourceProcessor;

    public Dispatcher() {
        routes = new HashMap<>();

        Map<HttpMethod, RequestProcessor> getItems = new HashMap<>();
        routes.put(HttpMethod.GET, new GetItemsProcessor());

        Map<HttpMethod, RequestProcessor> postItems = new HashMap<>();
        routes.put(HttpMethod.POST, new CreateItemsProcessor());

        Map<HttpMethod, RequestProcessor> deleteItems = new HashMap<>();
        routes.put(HttpMethod.DELETE, new DeleteItemsProccessor());

        Map<HttpMethod, RequestProcessor> putItems = new HashMap<>();
        routes.put(HttpMethod.PUT, new UpdateItemsProcessor());


        defaultNotFoundProcessor = new DefaultNotFoundProcessor();
        defaultStaticResourceProcessor = new DefaultStaticResourceProcessor();
    }

    public void execute(HttpRequest request, OutputStream output) throws IOException, SQLException {
        if (Files.exists(Paths.get("static/", request.getUri().substring(1)))) {
            defaultStaticResourceProcessor.execute(request, output);
            return;
        }
        if (!uri.equals(request.getUri())) {
            defaultNotFoundProcessor.execute(request, output);
            return;
        } else if (!routes.containsKey(request.getMethod())) {
            Path filePath = Paths.get("static/405.png");
            long size = Files.size(filePath);
            if (size > Config.maxResponseSize()) {
                throw new RuntimeException("too much response");
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
            String response = "" +
                    "HTTP/1.1 405 Method Not Allowed\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + size + "\r\n" +
                    "\r\n";
            output.write(response.getBytes(StandardCharsets.UTF_8));
            try (InputStream in = new BufferedInputStream(Files.newInputStream(filePath), 8 * 1024)) {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            output.flush();
        }
        try {
            routes.get(request.getMethod()).execute(request, output);
        } catch (BadRequestException e) {
            Path filePath = Paths.get("static/400.png");
            long size = Files.size(filePath);
            if (size > Config.maxResponseSize()) {
                throw new RuntimeException("too much response");
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
            String response = "" +
                    "HTTP/1.1 400 Bad Request\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + size + "\r\n" +
                    "\r\n";
            output.write(response.getBytes(StandardCharsets.UTF_8));
            try (InputStream in = new BufferedInputStream(Files.newInputStream(filePath), 8 * 1024)) {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            output.flush();
        } catch (Exception e) {
            Path filePath = Paths.get("static/500.png");
            long size = Files.size(filePath);
            if (size > Config.maxResponseSize()) {
                throw new RuntimeException("too much response");
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
            String response = "" +
                    "HTTP/1.1 500 Internal Server Error\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + size + "\r\n" +
                    "\r\n";
            output.write(response.getBytes(StandardCharsets.UTF_8));
            try (InputStream in = new BufferedInputStream(Files.newInputStream(filePath), 8 * 1024)) {
                byte[] buffer = new byte[8 * 1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            }
            output.flush();
        }
    }
}
