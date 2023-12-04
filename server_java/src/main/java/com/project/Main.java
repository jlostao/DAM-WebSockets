package com.project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

// Tutorials: http://tootallnate.github.io/Java-WebSocket/

/*
    WebSockets server, example of messages:

    From client to server:
        - Connect       { "type": "hello",  "name": "ScrumMaster3000"}
        - Flip card       { "type": "flip", "row": 1, "col": 1 , "name": "Gapy"}

    From server to client:
        - Flip card     { "type": "flip", "row": 1, "col": 1, "color": "Black" }
        - Permanent flip card    { "type": "permShow", "row": 1, "col": 1, "color": "Black" }
        - New turn    { "type": "newTurn", "plays": "Gapy", "waits": "UwU" "prePoints": 20000321342 } //Previous player's points
        - Clear     { "type": "clear"} clear the board
        - Winner    { "type": "winner", "player": "Gapy" }
 */

public class Main {

    public static void main (String[] args) throws InterruptedException, IOException {

        int port = 8888; 
        String localIp = getLocalIPAddress();
        System.out.println("Local server IP: " + localIp);

        // Deshabilitar SSLv3 per clients Android
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        ChatServer server = new ChatServer(port);
        server.runServerBucle();
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
}