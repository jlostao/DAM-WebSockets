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

public class AppData {

    private static final AppData INSTANCE = new AppData();
    private AppSocketClient socketClient;
    private String ip = "localhost";
    private String port = "8888";
    private String messages = "";

    private AppData() {
        try {
            URI uri = new URI("ws://" + ip + ":" + port);
            socketClient = new AppSocketClient(uri, new Draft_6455(), this);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static AppData getInstance() {
        return INSTANCE;
    }

    public void connectToServer() {
        socketClient.connect();
    }

    public void disconnectFromServer() {
        socketClient.close();
    }

    public void send(String msg) {
        socketClient.send(msg);
    }

    public void handleMessage(String message) {
        setMessages(getMessages() + "\n" + message);
    }

    // ... Rest of your getters, setters, and PropertyChangeSupport methods

}
