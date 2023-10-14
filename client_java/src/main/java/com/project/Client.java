package com.project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Scanner;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.enums.ReadyState;
import org.json.JSONObject;

public class Client extends WebSocketClient {
    public boolean running = true;
    public LinkedList<String> last5Messages = new LinkedList<>();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        int port = 8888;
        String host = "localhost";
        String location = "ws://" + host + ":" + port;

        Client client = getClient(location);

        while (client.running) {
            displayPrompt(client);
            String text = sc.nextLine();
            if (text.equalsIgnoreCase("exit")) {
                client.running = false;
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
                    String destId = text.substring(3, endIdx);
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

    @Override
    public void onMessage(String message) {
        last5Messages.add("Message received: " + message);
        if (last5Messages.size() > 5) {
            last5Messages.removeFirst();
        }
        displayPrompt(this);
    }

    public static void displayPrompt(Client client) {
        clearConsole();
        System.out.println("Connection: " + client.getReadyState());
        for (String msg : client.last5Messages) {
            System.out.println(msg);
        }
        System.out.print("Type a message (list, exit, to(id)message, broadcast message): ");
    }

    public static void clearConsole() {
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

    public Client(URI uri, Draft draft) {
        super(uri, draft);
    }

    static public Client getClient(String location) {
        Client client = null;

        try {
            client = new Client(new URI(location), (Draft) new Draft_6455());
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error: " + location + " is not a valid WebSocket URI");
        }

        return client;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from: " + getURI());
    }
    

    @Override
    public void onError(Exception ex) {
        System.out.println("WebSocket connection error.");
    }


    static public boolean isConnected (Client client) {
        return client.getReadyState() == ReadyState.OPEN;
    }

    public void reconnect() {
        System.out.println("Trying to reconnect...");
        close();
        connect();
    }  
}
