package com.project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Scanner;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.enums.ReadyState;
import org.json.JSONObject;

/*
    Examples d'instruccions:

    exit                > Exit the program
    reconnect           > Reconnect to the server
    list                > Get list of clients
    to(6a2)message      > Send a private message to a client
    broadcast message   > Send a message to everyone
    
 */

public class ChatClient {
    private Scanner sc = new Scanner(System.in);
    private LinkedList<String> _last5Messages = new LinkedList<>();
    private SocketsClient client;
    private String location;

    public ChatClient(String location) {
        this.client = getClient(location);
    }

    public void start() {
        boolean running = true;
        while (running) {
            displayPrompt();
            String text = sc.nextLine();
            if (text.equalsIgnoreCase("exit")) {
                running = false;
                client.close();
                break;
            } else if (text.equalsIgnoreCase("list")) {
                JSONObject obj = new JSONObject();
                obj.put("type", "list");
                client.send(obj.toString());
            } else if (text.startsWith("to(")) {
                int endIdx = text.indexOf(")");
                if (endIdx > -1) {
                    String destId = text.substring(3, endIdx);
                    String msg = text.substring(endIdx + 1);

                    JSONObject obj = new JSONObject();
                    obj.put("type", "private");
                    obj.put("destination", destId);
                    obj.put("value", msg);
                    client.send(obj.toString());
                }
            } else if (text.startsWith("broadcast ")) {
                int endIdx = text.indexOf(" ");
                if (endIdx > -1) {
                    String msg = text.substring(endIdx + 1);

                    JSONObject obj = new JSONObject();
                    obj.put("type", "broadcast");
                    obj.put("value", msg);
                    client.send(obj.toString());
                }
            }
        }

        if (client != null) { client.close(); }
    }

    public SocketsClient getClient(String location) {
        SocketsClient client = null;

        try {
            client = new SocketsClient(
                new URI(location), 
                (ServerHandshake handshake) -> { this.onMessage("Connection opened"); },
                (String message) -> { this.onMessage(message); },
                (JSONObject closeInfo) -> { this.onMessage("Connection closed"); },
                (Exception ex) -> { this.onMessage("Error: " + ex.getMessage()); }
            );
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error: " + location + " is not a valid WebSocket URI");
        }

        return client;
    }

    public void displayPrompt() {
        clearConsole();
        System.out.println("Connection: " + client.getReadyState());
        for (String msg : _last5Messages) {
            System.out.println(msg);
        }
        System.out.print("Type a message (list, exit, to(id)message, broadcast message): ");
    }

    public void clearConsole() {
        String os = System.getProperty("os.name").toLowerCase();

        try {
            if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else if (os.contains("mac") || os.contains("unix") || os.contains("linux")) {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            } else {
                // Fallback per si no es pot detectar l'OS
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected (SocketsClient client) {
        return client.getReadyState() == ReadyState.OPEN;
    }

    private void onMessage(String message) {
        System.out.println("Message received: " + message);
        _last5Messages.add("Message received: " + message);
        if (_last5Messages.size() > 5) {
            _last5Messages.removeFirst();
        }
        displayPrompt();
    }
}