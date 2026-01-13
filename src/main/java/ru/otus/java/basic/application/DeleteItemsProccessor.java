package ru.otus.java.basic.application;

import com.google.gson.Gson;
import ru.otus.java.basic.HttpRequest;
import ru.otus.java.basic.processors.RequestProcessor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class DeleteItemsProccessor implements RequestProcessor {
    @Override
    public void execute(HttpRequest request, OutputStream output) throws IOException, SQLException {
        Gson gson = new Gson();
        ItemsStorage itemsStorage = new ItemsStorage();
        itemsStorage.deleteItem(request.getPath());
        String response = "" +
                "HTTP/1.1 204 No Content\r\n" +
                "\r\n";
        output.write(response.getBytes(StandardCharsets.UTF_8));
    }
}
