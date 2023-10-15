package com.project;

import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CtrlLayoutDisconnected {

    @FXML
    private TextField ipTextField;

    @FXML
    private TextField portTextField;

    private AppData appData;

    public void initialize() {
        appData = AppData.getInstance();

        try {
            appData.setIp(appData.getLocalIPAddress());
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        ipTextField.setText(appData.getIp());
        portTextField.setText(appData.getPort());
    }

    @FXML
    private void connectToServer() {
        appData.setIp(ipTextField.getText());
        appData.setPort(portTextField.getText());
        appData.connectToServer();

    }
}
