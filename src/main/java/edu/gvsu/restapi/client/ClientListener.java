package edu.gvsu.restapi.client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ClientListener implements Runnable {

    private Integer inputPort;

    public ClientListener(Integer listenPort) {
        this.inputPort = listenPort;
    }


    public void run() {
        ServerSocket echoServer = null;
        Socket clientSocket = null;

        try {
            echoServer = new ServerSocket(inputPort);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
//                System.out.println("Waiting for connections");
                clientSocket = echoServer.accept();
                Thread thread = new Thread(new ProcessIncomingRequest(clientSocket));
                thread.start();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}

