package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChatServer extends WebSocketServer {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    Board bd = new Board();

    public ChatServer (int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        String host = getAddress().getAddress().getHostAddress();
        int port = getAddress().getPort();
        System.out.println("WebSockets server running at: ws://" + host + ":" + port);
        System.out.println("Type 'exit' to stop and exit server.");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String clientId = getConnectionId(conn);

        

        JSONObject objId = new JSONObject("{}");
        objId.put("type", "id");
        objId.put("from", "server");
        objId.put("value", clientId);
        conn.send(objId.toString()); 

        sendList(conn);

        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "connected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        if (bd.players.size() == 1) {
            bd.startTurn();
            broadcast("{ \"type\": \"newTurn\", \"plays\": \"" + bd.players.get(bd.turn) + "\", \"waits\": \"\", \"prePoints\": 0 }");
        }

        String host = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        System.out.println("New client (" + clientId + "): " + host);
        
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String clientId = getConnectionId(conn);

        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        System.out.println("Client disconnected '" + clientId + "'");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println(message);
        String clientId = getConnectionId(conn);
        try {
            JSONObject objRequest = new JSONObject(message);
            String type = objRequest.getString("type");
            if (type.equalsIgnoreCase("hello")) {
                // save players while they are less than 2
                if(bd.players.size() < 2){
                    String userName = objRequest.getString("name");
                    System.out.println(userName);
                    bd.players.add(userName);
                    if(bd.players.size() == 2){
                        startTurn();
                    }
                }
                

            } else if (type.equalsIgnoreCase("flip")) {
                int row = objRequest.getInt("row");
                int col = objRequest.getInt("col");

                broadcast("{ \"type\": \"flip\", \"row\": "+row+", \"col\": "+col+", \"color\": \""+bd.board[row][col]+"\" }");
            }   else if (type.equalsIgnoreCase("getBoard")) {
                sendBoard(conn);
            }
                /* if(bd.players.get(bd.turn).equals(objRequest.getString("name"))){
                    
                    // Check if is the last flip
                     if(bd.flipCard(row, col)){
                        broadcast("{ \"type\": \"flip\", \"row\": "+row+", \"col\": "+col+", \"color\": \""+bd.board[row][col]+"\" }");
    
                        // Check if the two cards are the same color
                        if(bd.board[bd.firstSelect.get(0)][bd.firstSelect.get(1)].equals(bd.board[row][col])){
                            bd.points.set(bd.turn, bd.points.get(bd.turn)+1);
                            bd.showBoard[bd.firstSelect.get(0)][bd.firstSelect.get(1)] = 1;
                            bd.showBoard[row][col] = 1;
                            broadcast("{ \"type\": \"permShow\", \"row\": "+row+", \"col\": "+col+", \"color\": \""+bd.board[row][col]+"\" }");
                            broadcast("{ \"type\": \"permShow\", \"row\": "+bd.firstSelect.get(0)+", \"col\": "+bd.firstSelect.get(1)+", \"color\": \""
                            +bd.board[bd.firstSelect.get(0)][bd.firstSelect.get(1)]+"\" }");
                            
                            if(bd.hasEnded()){
                                int highestScore = 0;
                                String winnerPlayer = "";
                                for (int i = 0; i < bd.points.size(); i++) {
                                    if (bd.points.get(i) > highestScore) {
                                        highestScore = bd.points.get(i);
                                        winnerPlayer = bd.players.get(i);
                                    }
                                }
                                broadcast("{ \"type\": \"winner\", \"player\": \""+winnerPlayer+"\" }");
                                bd.endedGame = true;
                            }
    
                        } else {
                            bd.showBoard[bd.firstSelect.get(0)][bd.firstSelect.get(1)] = 0;
                            bd.showBoard[row][col] = 0;
                        } 
                        if(!bd.endedGame){
                            bd.turnFlips = 0;
                            try {
                                Thread.sleep(1000); 
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            broadcast("{ \"type\": \"clear\"} ");
                            bd.startTurn();
                        }
                    } else if(bd.turnFlips == 1){
                        broadcast("{ \"type\": \"flip\", \"row\": "+row+", \"col\": "+col+", \"color\": \""+bd.board[row][col]+"\" }");
                    } 
                }
            } */
            

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
            System.out.println("Server Start");
            start();
            while (running) {
                String line;
                line = in.readLine();
                if (line.equals("exit")) {
                    running = false;
                }
            } 
            System.out.println("Server Stop");
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

    private void sendBoard(WebSocket conn) {
    JSONObject objResponse = new JSONObject();
    objResponse.put("type", "board");
    objResponse.put("from", "server");

    // Convert the 2D array to a JSON array of JSON arrays
    JSONArray boardArray = new JSONArray();
    for (int i = 0; i < bd.board.length; i++) {
        JSONArray rowArray = new JSONArray();
        for (int j = 0; j < bd.board[i].length; j++) {
            rowArray.put(bd.board[i][j]);
        }
        boardArray.put(rowArray);
    }

    objResponse.put("board", boardArray);
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
        bd.newTurn();
        // Send mssg to notify new turn
        broadcast("{ \"type\": \"newTurn\", \"plays\": \""+ bd.players.get(bd.turn)+"\", \"waits\": \""+ bd.players.get((bd.turn+1)%2)+"\", \"prePoints\": "+ bd.points.get((bd.turn+1)%2)+" }");
    }

    

}