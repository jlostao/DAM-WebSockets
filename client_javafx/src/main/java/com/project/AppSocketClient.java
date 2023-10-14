package com.project;

import java.net.URI;
import java.util.LinkedList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class AppSocketClient extends WebSocketClient {

    private LinkedList<String> _lastMessages;

    public AppSocketClient(URI uri) {
        super(uri, (Draft) new Draft_6455());
        _lastMessages = new LinkedList<>();
    }

    @Override
    public void onMessage(String message) {
        _lastMessages.add("Message received: " + message);
        if (_lastMessages.size() > 50) {
            _lastMessages.removeFirst();
        }
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

    public LinkedList<String> getLastMessages() {
        return _lastMessages;
    }
}
