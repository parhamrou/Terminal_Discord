package  Discord.Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import CommonClasses.*;

public class ClientHandler implements Runnable {

    
    private ObjectInputStream oInputStream;
    private ObjectOutputStream oOutputStream;
    private Socket socket;
    private User user;

    public ClientHandler(Socket socket) {
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
                clientRequest = (Request) oInputStream.readObject();
                System.out.println(clientRequest.toString());
                switch (clientRequest) {
                    case CHECK_USERNAME:
                        userMap = (HashMap<String, String>) oInputStream.readObject();
                        oOutputStream.writeObject(DataManager.checkUserName(userMap));
                        break;
                    case SIGN_UP:
                        user = (User) oInputStream.readObject();
                        oOutputStream.writeObject(DataManager.signUp(user));
                        break;
                    case SIGN_IN: 
                        userMap = (HashMap<String, String>) oInputStream.readObject();
                        user = DataManager.signIn(userMap);
                        oOutputStream.writeObject(user);
                        break;
                    case SERVER_LIST:
                        oOutputStream.writeObject(user.getServersNames());
                        clientRequest = (Request) oInputStream.readObject();
                        if (clientRequest == Request.ENTER_SERVER) {
                            String serverName = (String) oInputStream.readObject(); // getting the server's name from client
                            boolean doesServerExist = user.doesServerExist(serverName);
                            oOutputStream.writeObject(doesServerExist);
                            if (doesServerExist) {
                                oOutputStream.writeObject(user.getServerPort(serverName));
                            }
                        }
                        break;
                    case CHECK_SERVER_NAME:
                        String serverName = (String) oInputStream.readObject();
                        oOutputStream.writeObject(DataManager.isServerNameDuplicated(serverName));
                        Request request = (Request) oInputStream.readObject();
                        if (request == Request.CREATE_SERVER) {
                            User user = (User) oInputStream.readObject();
                            Server server = new Server(serverName, user);
                            DataManager.addServer(server);
                            server.start();
                        }
                        break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error");
        }
    }
}


