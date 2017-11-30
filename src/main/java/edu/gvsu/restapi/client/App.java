package edu.gvsu.restapi.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;


public class App {
    public static void main(String args[]) {

        SampleRESTClient client = new SampleRESTClient();
        boolean status = true;
        boolean running = true;
        String option;
        String username = args[0];
        String serverHost = "localhost";
        try {
            InetAddress ip = InetAddress.getLocalHost();
            serverHost = ip.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


        BufferedReader is = new BufferedReader(new InputStreamReader(System.in));

        try {


            //Get the port for the client
            BufferedReader portInput = new BufferedReader(new InputStreamReader(System.in));
            Integer clientPort = getListenPort(portInput);


            //creates new reginfo object for this client
            RegistrationInfo user = new RegistrationInfo();
            user.setUserName(args[0]);
            user.setStatus(status);
            user.setHost(serverHost);
            user.setPort(clientPort);

            //registers with Cloud service
            client.register(user);

            //start listener here
            Thread listener = new Thread(new ClientListener(clientPort));
            listener.start();

            //Menu Structure
            while (running) {

                printGreeting();

                option = is.readLine();
                String[] tokens = option.split(" ");

                switch (tokens[0]) {
                    case "friends":

                        for (RegistrationInfo RegInfo : client.listRegisteredUsers()) {
                            System.out.println(RegInfo.getUserName() + " -- Status: " + RegInfo.getStatus());
                        }

                        break;
                    case "talk":
                        if (tokens.length < 3) {
                            System.out.println("must have a message to send");
                            break;
                        }
                        String msg = buildMessage(tokens);

                        String targetName = tokens[1];
                        sendMessage(msg, client, targetName, username);

                        break;

                    case "broadcast":

                        String message = buildBroadcastMessage(tokens);
                        for (RegistrationInfo info : client.listRegisteredUsers()) {
                            if (!info.getUserName().equals(username)) {
                                sendMessage(message, client, info.getUserName(), username);
                            }

                        }

                        break;

                    case "busy":
                        client.setStatus(username, false);
                        client.lookup(username);
                        System.out.println("Status set to Busy\n");
                        break;

                    case "available":
                        client.setStatus(username, true);
                        client.lookup(username);

                        System.out.println("Status set to Available\n");
                        break;

                    case "exit":
                        running = false;
                        client.unregister(username);
                        System.out.println("\nLater!");
                        System.exit(0);
                        break;

                    default:
                        System.out.println("Invalid command.");
                        break;
                }


            }
        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();

        }


    }

    private static void printGreeting() {

        System.out.println("\nPlease make a selection from the following items\n" +
                "friends - get list of friends and their availability\n" +
                "talk [username] [message] - Send a message to a user\n" +
                "broadcast\n" +
                "busy: Sets your status to Busy.\n" +
                "available: Sets your status to Available\n" +
                "exit\n" +
                "----------------------------------------------------\n");
    }

    //builds a message from arguements given for Broadcast.
    private static String buildBroadcastMessage(String[] tokens) {

        String message = "";

        //rebuild the message
        for (int i = 1; i < tokens.length; i++) {
            message += tokens[i] + " ";
        }
        String trimmedMessage = message.trim();

        return trimmedMessage;
    }

    //constructs the message the user wants to send from the arguements given
    private static String buildMessage(String[] tokens) {

        String message = "";

        //rebuild the message
        for (int i = 2; i < tokens.length; i++) {
            message += tokens[i] + " ";
        }
        String trimmedMessage = message.trim();

        return trimmedMessage;
    }

    //opens a socket, sends a message anc closes the socket
    private static void sendMessage(String message, SampleRESTClient client, String targetName, String user) {

        try {
            RegistrationInfo talkTarget = client.lookup(targetName);
            if (talkTarget != null) {
                Boolean busyStatus = talkTarget.getStatus();

                if (busyStatus) {

                    String targetHost = talkTarget.getHost();
                    Integer targetPort = talkTarget.getPort();

                    try {
                        Socket clientSocket = new Socket(targetHost, targetPort);
                        DataOutputStream tx = new DataOutputStream(clientSocket.getOutputStream());
                        tx.writeBytes(user + ": " + message);


                        tx.close();
                        clientSocket.close();

                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
            }
        } catch (RemoteException e) {
            System.out.println(e);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static Integer getListenPort(BufferedReader is) {
        try {
            System.out.println("Input Port for client\n");
            String input = is.readLine();
            return Integer.parseInt(input);
        } catch (IOException e) {
            System.out.println("!--Defaulting to listen port 9999--!\n");
            return 9999;
        }
    }

}


