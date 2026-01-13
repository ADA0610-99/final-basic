package ru.otus.java.basic.processors;

import ru.otus.java.basic.HttpRequest;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public interface RequestProcessor {
    void execute(HttpRequest request, OutputStream output) throws IOException, SQLException;
}

