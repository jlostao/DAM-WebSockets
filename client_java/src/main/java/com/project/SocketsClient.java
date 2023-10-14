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

public class SocketsClient extends WebSocketClient {

    private LinkedList<String> _last5Messages;

    public SocketsClient(URI uri) {
        super(uri, (Draft) new Draft_6455());
        _last5Messages = new LinkedList<>();
    }

    @Override
    public void onMessage(String message) {
        _last5Messages.add("Message received: " + message);
        if (_last5Messages.size() > 5) {
            _last5Messages.removeFirst();
        }
        Main.displayPrompt(this);
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

    public LinkedList<String> getLast5Messages() {
        return _last5Messages;
    }
}
