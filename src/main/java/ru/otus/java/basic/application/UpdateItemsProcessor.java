package ru.otus.java.basic.application;

import com.google.gson.Gson;
import ru.otus.java.basic.HttpRequest;
import ru.otus.java.basic.config.Config;
import ru.otus.java.basic.processors.RequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class UpdateItemsProcessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException, SQLException {
        Gson gson = new Gson();
        Item item = gson.fromJson(request.getBody(), Item.class);
        ItemsStorage itemsStorage = new ItemsStorage();
        itemsStorage.updateItem(item);
        byte[] bodyBytes = gson.toJson(item).getBytes(StandardCharsets.UTF_8);
        if (bodyBytes.length > Config.maxResponseSize()) {
            throw new RuntimeException("too much response");
        }
        String response = "" +
                "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json\r\n" +
                "\r\n" +
                gson.toJson(item);
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
