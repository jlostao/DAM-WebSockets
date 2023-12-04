package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class ChatServer extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    Operations op = new Operations();

    public ChatServer (int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        // Quan el servidor s'inicia
        String host = getAddress().getAddress().getHostAddress();
        int port = getAddress().getPort();
        System.out.println("WebSockets server running at: ws://" + host + ":" + port);
        System.out.println("Type 'exit' to stop and exit server.");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
        op.printBoard();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Quan un client es connecta
        String clientId = getConnectionId(conn);

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

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Quan un client es desconnecta
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

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(message);
        // Quan arriba un missatge
        String clientId = getConnectionId(conn);
        try {
            JSONObject objRequest = new JSONObject(message);
            String type = objRequest.getString("type");
            if (type.equalsIgnoreCase("hello")) {
                System.out.println("ola");
                // save players while they are less than 2
                if(op.players.size() < 2){
                    String userName = objRequest.getString("name");
                    System.out.println(userName);
                    op.players.add(userName);
                    if(op.players.size() == 2){
                        startTurn();
                    }
                }
                

            } else if (type.equalsIgnoreCase("flip")) {
                // Check if is the correct player
                System.out.println(op.players.get(op.turn) + " = " + objRequest.getString("name"));
                if(op.players.get(op.turn).equals(objRequest.getString("name"))){
                    System.out.println("he entrado");
                    int row = objRequest.getInt("row");
                    int col = objRequest.getInt("col");
                    
                    // Check if is the last flip
                    if(op.flipCard(row, col)){
                        broadcast("{ \"type\": \"flip\", \"row\": "+row+", \"col\": "+col+", \"color\": \""+op.board[row][col]+"\" }");

                        // Check if the two cards are the same color
                        if(op.board[op.firstSelect.get(0)][op.firstSelect.get(1)].equals(op.board[row][col])){
                            op.points.set(op.turn, op.points.get(op.turn)+1);
                            op.showBoard[op.firstSelect.get(0)][op.firstSelect.get(1)] = 1;
                            op.showBoard[row][col] = 1;
                            broadcast("{ \"type\": \"permShow\", \"row\": "+row+", \"col\": "+col+", \"color\": \""+op.board[row][col]+"\" }");
                            broadcast("{ \"type\": \"permShow\", \"row\": "+op.firstSelect.get(0)+", \"col\": "+op.firstSelect.get(1)+", \"color\": \""
                            +op.board[op.firstSelect.get(0)][op.firstSelect.get(1)]+"\" }");
                            
                            if(op.hasEnded()){
                                int highestScore = 0;
                                String winnerPlayer = "";
                                for (int i = 0; i < op.points.size(); i++) {
                                    if (op.points.get(i) > highestScore) {
                                        highestScore = op.points.get(i);
                                        winnerPlayer = op.players.get(i);
                                    }
                                }
                                broadcast("{ \"type\": \"winner\", \"player\": \""+winnerPlayer+"\" }");
                                op.endedGame = true;
                            }

                        }else {
                            op.showBoard[op.firstSelect.get(0)][op.firstSelect.get(1)] = 0;
                            op.showBoard[row][col] = 0;
                        } 
                        if(!op.endedGame){
                             op.turnFlips = 0;
                            try {
                                Thread.sleep(1000); 
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            broadcast("{ \"type\": \"clear\"} ");
                            startTurn();
                        }
                       
                    }else if(op.turnFlips == 1){
                        broadcast("{ \"type\": \"flip\", \"row\": "+row+", \"col\": "+col+", \"color\": \""+op.board[row][col]+"\" }");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Quan hi ha un error
        ex.printStackTrace();
    }

    public void runServerBucle () {
        boolean running = true;
        try {
            System.out.println("Starting server");
            start();
            while (running) {
                String line;
                line = in.readLine();
                if (line.equals("exit")) {
                    running = false;
                }
            } 
            System.out.println("Stopping server");
            stop(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  
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
        int length = getConnections().size();
        String[] clients = new String[length];
        int cnt = 0;

        for (WebSocket ws : getConnections()) {
            clients[cnt] = getConnectionId(ws);               
            cnt++;
        }
        return clients;
    }

    public WebSocket getClientById (String clientId) {
        for (WebSocket ws : getConnections()) {
            String wsId = getConnectionId(ws);
            if (clientId.compareTo(wsId) == 0) {
                return ws;
            }               
        }
        
        return null;
    }

    public void startTurn(){
        op.newTurn();
        // Send mssg to notify new turn
        broadcast("{ \"type\": \"newTurn\", \"plays\": \""+ op.players.get(op.turn)+"\", \"waits\": \""+ op.players.get((op.turn+1)%2)+"\", \"prePoints\": "+ op.points.get((op.turn+1)%2)+" }");
    }

}