package ru.otus.java.basic;

import ru.otus.java.basic.application.ItemsStorage;
import ru.otus.java.basic.config.Config;
import ru.otus.java.basic.exceptions_handling.BadRequestException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private int port;
    private Dispatcher dispatcher;

    public HttpServer(int port) {
        this.port = port;
        this.dispatcher = new Dispatcher();
    }

    public void start() {
        ExecutorService serv = Executors.newFixedThreadPool(Config.threadPoolSize());

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);

            while (true) {
                Socket socket = serverSocket.accept();

                serv.execute(() -> {
                    String t = Thread.currentThread().getName();
                    System.out.println("Обрабатываю в потоке: " + t);
                    try (Socket s = socket) {
                        InputStream in = s.getInputStream();
                        ByteArrayOutputStream bs = new ByteArrayOutputStream();
                        byte[] buf = new byte[8 * 1024];
                        int headerEnd = -1;
                        while (headerEnd == -1) {
                            int n = in.read(buf);
                            if (n < 0) {
                                return;
                            }
                            bs.write(buf, 0, n);
                            if (bs.size() > Config.maxRequestSize()) {
                                throw new BadRequestException("too much response");
                            }
                            headerEnd = getHeaderEnd(bs.toByteArray());
                        }
                        byte[] data = bs.toByteArray();
                        int contentLength = getContentLength(data);
                        int bodySize = data.length - headerEnd;
                        while (bodySize < contentLength) {
                            int n = in.read(buf);
                            if (n < 0) break;
                            bs.write(buf, 0, n);
                            if (bs.size() > Config.maxRequestSize()) {
                                throw new BadRequestException("too much response");
                            }
                            data = bs.toByteArray();
                            bodySize = data.length - headerEnd;
                        }
                        String rawRequest = new String(bs.toByteArray(), StandardCharsets.UTF_8);
                        HttpRequest request = new HttpRequest(rawRequest);
                        dispatcher.execute(request, s.getOutputStream());
                    } catch (BadRequestException e) {
                        e.printStackTrace();
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int getHeaderEnd(byte[] bytes) {
        for (int i = 0; i < bytes.length - 3; i++) {
            if (bytes[i] == '\r' && bytes[i + 1] == '\n' && bytes[i + 2] == '\r' && bytes[i + 3] == '\n') {
                return i + 4;
            }
        }
        return -1;
    }

    private static int getContentLength(byte[] requestBytes) {
        String head = new String(requestBytes, StandardCharsets.UTF_8);
        int headerEnd = head.indexOf("\r\n\r\n");
        if (headerEnd == -1) return 0;
        String headers = head.substring(0, headerEnd);
        for (String line : headers.split("\r\n")) {
            if (line.toLowerCase().startsWith("content-length:")) {
                String v = line.substring("content-length:".length()).trim();
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

}

