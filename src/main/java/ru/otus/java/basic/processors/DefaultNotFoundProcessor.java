package ru.otus.java.basic.processors;

import ru.otus.java.basic.HttpRequest;
import ru.otus.java.basic.config.Config;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DefaultNotFoundProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException {
        Path filePath = Paths.get("static/404.png");
        long size = Files.size(filePath);
        if (size > Config.maxResponseSize()) {
            throw new RuntimeException("too much response");
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) contentType = "application/octet-stream";
        String response = "" +
                "HTTP/1.1 404 Not Found\r\n" +
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