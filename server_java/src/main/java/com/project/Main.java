package com.project;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;


// Tutorials: http://tootallnate.github.io/Java-WebSocket/

/*
    WebSockets server, example of messages:

    From client to server:
        - List of clients       { "type": "list" }
        - Private message       { "type": "private", "value": "Hello 002", "destination": "002" }
        - Broadcast message     { "type": "broadcast", "value": "Hello everyone" }

    From server to client:
        - Welcome message       { "type": "private", "from": "server", "value": "Welcome to the chat server" }
        - Client Id             { "type": "id", "from": "server", "value": "002" }
        - List of clients       { "type": "list", "from": "server", "list": ["001", "002", "003"] }
        - Private message       { "type": "private", "from": "001", "value": "Hello 002" }
        - Broadcast message     { "type": "broadcast", "from": "001", "value": "Hello everyone" }
        - Client connected      { "type": "connected", "from": "server", "id": "001" }
        - Client disconnected   { "type": "disconnected", "from": "server", "id": "001" }
 */

public class Main extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main (String[] args) throws InterruptedException, IOException {

        int port = 8888; 
        boolean running = true;
        String localIp = getLocalIPAddress();

        // Deshabilitar SSLv3 per clients Android
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        Main socket = new Main(port);
        socket.start();
        System.out.println("WebSockets server running at: ws://" + localIp + ":" + socket.getPort());

        while (running) {
            String line = in.readLine();
            socket.broadcast(line);
            if (line.equals("exit")) {
                running = false;
            }
        }    

        System.out.println("Stopping server");
        socket.stop(1000);
    }

    public Main (int port) throws UnknownHostException {
        super(new InetSocketAddress(port));
    }

    public static String getLocalIPAddress() throws SocketException, UnknownHostException {
        
        String localIp = "";
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress ia = inetAddresses.nextElement();
                if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia.isSiteLocalAddress()) {
                    System.out.println(ni.getDisplayName() + ": " + ia.getHostAddress());
                    localIp = ia.getHostAddress();
                    // Si hi ha múltiples direccions IP, es queda amb la última
                }
            }
        }

        // Si no troba cap direcció IP torna la loopback
        if (localIp.compareToIgnoreCase("") == 0) {
            localIp = InetAddress.getLocalHost().getHostAddress();
        }
        return localIp;
    }

    // Quan un client es connecta
    @Override
    public void onOpen (WebSocket conn, ClientHandshake handshake) {

        String clientId = getConnectionId(conn);

        // Saludem personalment al nou client
        JSONObject objWlc = new JSONObject("{}");
        objWlc.put("type", "private");
        objWlc.put("from", "server");
        objWlc.put("value", "Welcome to the chat server");
        conn.send(objWlc.toString()); 

        // Li enviem el seu identificador
        JSONObject objId = new JSONObject("{}");
        objId.put("type", "id");
        objId.put("from", "server");
        objId.put("value", clientId);
        conn.send(objId.toString()); 

        // Enviem al client la llista amb tots els clients connectats
        sendList(conn);

        // Enviem la direcció URI del nou client a tothom 
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "connected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        // Mostrem per pantalla (servidor) la nova connexió
        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println("New client (" + clientId + "): " + host);
    }

    // Quan un client es desconnecta
    @Override
    public void onClose (WebSocket conn, int code, String reason, boolean remote) {

        String clientId = getConnectionId(conn);

        // Informem a tothom que el client s'ha desconnectat
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println("Client disconnected '" + clientId + "'");
    }

    // Quan el servidor rep un missatge d'un client
    @Override
    public void onMessage (WebSocket conn, String message) {

        String clientId = getConnectionId(conn);

        try {
            JSONObject objRequest = new JSONObject(message);
            String type = objRequest.getString("type");

            if (type.equalsIgnoreCase("list")) {
                // El client demana la llista de tots els clients
                System.out.println("Client '" + clientId + "'' requests list of clients");
                sendList(conn);

            } else if (type.equalsIgnoreCase("private")) {
                // El client envia un missatge privat a un altre client
                System.out.println("Client '" + clientId + "'' sends a private message");

                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "private");
                objResponse.put("from", clientId);
                objResponse.put("value", objRequest.getString("value"));

                String destination = objRequest.getString("destination");
                WebSocket desti = getClientById(destination);

                if (desti != null) {
                    desti.send(objResponse.toString()); 
                }
                
            } else if (type.equalsIgnoreCase("broadcast")) {
                // El client envia un missatge a tots els clients
                System.out.println("Client '" + clientId + "'' sends a broadcast message to everyone");

                JSONObject objResponse = new JSONObject("{}");
                objResponse.put("type", "broadcast");
                objResponse.put("from", clientId);
                objResponse.put("value", objRequest.getString("value"));
                broadcast(objResponse.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError (WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart () {
        // S'inicia el servidor
        System.out.println("Type 'exit' to stop and exit server.");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    public void sendList (WebSocket conn) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "list");
        objResponse.put("from", "server");
        objResponse.put("list", getClients());
        conn.send(objResponse.toString()); 
    }

    public String getConnectionId (WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }

    public String[] getClients () {
        int length = this.getConnections().size();
        String[] clients = new String[length];
        int cnt = 0;

        for (WebSocket ws : this.getConnections()) {
            clients[cnt] = getConnectionId(ws);               
            cnt++;
        }
        return clients;
    }

    public WebSocket getClientById (String clientId) {
        for (WebSocket ws : this.getConnections()) {
            String wsId = getConnectionId(ws);
            if (clientId.compareTo(wsId) == 0) {
                return ws;
            }               
        }
        
        return null;
    }
}