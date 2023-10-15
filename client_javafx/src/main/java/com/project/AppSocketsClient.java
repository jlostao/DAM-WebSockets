package com.project;

import java.net.URI;
import java.util.function.Consumer;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class AppSocketsClient extends WebSocketClient {

    private Consumer<ServerHandshake> callBackOnOpen;
    private Consumer<String> callBackOnMessage;
    private Consumer<JSONObject> callBackOnClose;
    private Consumer<Exception> callBackOnError;

    public AppSocketsClient(URI uri, Consumer<ServerHandshake> onOpen, Consumer<String> onMessage, Consumer<JSONObject> onClose, Consumer<Exception> onError) {
        super(uri, (Draft) new Draft_6455());
        this.callBackOnOpen = onOpen;
        this.callBackOnMessage = onMessage;
        this.callBackOnClose = onClose;
        this.callBackOnError = onError;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        callBackOnOpen.accept(handshake);
    }

    @Override
    public void onMessage(String message) {
        callBackOnMessage.accept(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        JSONObject closeInfo = new JSONObject();
        closeInfo.put("code", code);
        closeInfo.put("reason", reason);
        closeInfo.put("remote", remote);
        callBackOnClose.accept(closeInfo);
    }
    
    @Override
    public void onError(Exception ex) {
        callBackOnError.accept(ex);
    }
}
