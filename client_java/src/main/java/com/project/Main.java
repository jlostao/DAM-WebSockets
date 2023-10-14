package com.project;

public class Main {
    public static void main(String[] args) {
        int port = 8888;
        String host = "localhost";
        String location = "ws://" + host + ":" + port;

        ChatClient chatClient = new ChatClient(location);
        chatClient.start();
    }
}