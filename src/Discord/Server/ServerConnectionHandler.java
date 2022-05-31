package  Discord.Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import CommonClasses.*;

public class ServerConnectionHandler implements Runnable {

    
    private ObjectInputStream oInputStream;
    private ObjectOutputStream oOutputStream;
    private Socket socket;

    public ServerConnectionHandler(Socket socket) {
        this.socket = socket;
        try {
            oOutputStream = new ObjectOutputStream(socket.getOutputStream());
            oInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("I/O Error!");
        }
    }

    @Override
    public void run() {
        try {
            Request clientRequest;
            HashMap<String, String> userMap;
            while (true) {
                System.out.println("This print is for testing");
                System.out.println("waiting for a new request.");
                clientRequest = (Request) oInputStream.readObject();
                System.out.println(clientRequest.toString());
                switch (clientRequest) {
                    case CHECK_USERNAME:
                        userMap = (HashMap<String, String>) oInputStream.readObject();
                        System.out.println("printing after getting map for username checking:");
                        System.out.println(userMap);
                        oOutputStream.writeObject(AccountManager.checkUserName(userMap));
                        break;
                    case SIGN_UP:
                        User user = (User) oInputStream.readObject();
                        oOutputStream.writeObject(AccountManager.signUp(user));
                        break;
                    case SIGN_IN: 
                        System.out.println("Waiting for map...");
                        userMap = (HashMap<String, String>) oInputStream.readObject();
                        System.out.println("Printing passed map:");
                        System.out.println(userMap);
                        oOutputStream.writeObject(AccountManager.signIn(userMap));
                        break;
                }
                System.out.println("The request is answered!");
                System.out.println("finish");
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error");
        }
    }
}


