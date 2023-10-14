package com.project;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class AppSocketClient extends WebSocketClient {

    private AppData appData;

    public AppSocketClient(URI serverUri, Draft protocolDraft, AppData appData) {
        super(serverUri, protocolDraft);
        this.appData = appData;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        // Handle connection opened
    }

    @Override
    public void onMessage(String message) {
        appData.handleMessage(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // Handle connection closed
    }

    @Override
    public void onError(Exception ex) {
        // Handle error
    }

    public void send(String msg) {
        JSONObject obj = new JSONObject();
        obj.put("type", "broadcast");
        obj.put("value", msg);
        send(obj.toString());
    }
}
