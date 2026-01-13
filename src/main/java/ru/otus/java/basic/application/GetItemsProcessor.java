package ru.otus.java.basic.application;

import com.google.gson.Gson;
import ru.otus.java.basic.HttpRequest;
import ru.otus.java.basic.config.Config;
import ru.otus.java.basic.processors.RequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

public class GetItemsProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException, SQLException {
        // GET /api/v1/items?id=10
        // GET /api/v1/items
        Gson gson = new Gson();
        ItemsStorage storage = new ItemsStorage();
        String itemsJson;
        if (request.getPath() != null) {
            itemsJson = gson.toJson(storage.getItems(request.getPath()));
        } else {
            itemsJson = gson.toJson(storage.getItems());
        }
        byte[] bodyBytes = itemsJson.getBytes(StandardCharsets.UTF_8);
        if (bodyBytes.length > Config.maxResponseSize()) {
            throw new RuntimeException("too much response");
        }
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                itemsJson;
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
