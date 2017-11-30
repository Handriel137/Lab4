package edu.gvsu.restapi.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ProcessIncomingRequest implements Runnable{
    private Socket clientSocket;

    public ProcessIncomingRequest(Socket clientSocket) {
        super();
        this.clientSocket = clientSocket;
    }

   public void run() {
        String line;
        String username;
        BufferedReader is;
        PrintStream os;

        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());

            while (true) {
                line = is.readLine();
                if (line == null) {
                    break;
                }
                System.out.println(line);
//                os.println("second line\n" + line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
