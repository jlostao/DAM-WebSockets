package com.project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.enums.ReadyState;
import org.json.JSONObject;

public class SocketsClient extends WebSocketClient {

    private Consumer<String> callBack;

    public SocketsClient(URI uri, Consumer<String> onMessage) {
        super(uri, (Draft) new Draft_6455());
        this.callBack = onMessage;
    }

    @Override
    public void onMessage(String message) {
        callBack.accept(message);
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
}
