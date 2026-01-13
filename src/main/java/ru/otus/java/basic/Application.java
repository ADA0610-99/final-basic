package ru.otus.java.basic;

import ru.otus.java.basic.config.Config;


public class Application {
    public static void main(String[] args) {
        new HttpServer(Config.serverPort()).start();
    }
}