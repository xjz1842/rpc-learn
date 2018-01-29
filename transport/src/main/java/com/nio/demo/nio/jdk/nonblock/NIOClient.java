package com.nio.demo.nio.jdk.nonblock;

public class NIOClient {

    public static void main(String[] args) {

        int port = 8080;

        new Thread(new NIOClientHandler("127.0.0.1", port)).start();
    }
}
