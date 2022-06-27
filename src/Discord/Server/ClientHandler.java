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
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            Request clientRequest;
            HashMap<String, String> userMap;
            while (true) {
                System.out.println("Waiting for a request from the user "  + "...");
                clientRequest = (Request) oInputStream.readObject();
                System.out.println("Thread ID:" + Thread.currentThread().getId());
                System.out.println("Request: " + clientRequest.toString());
                System.out.println("______________________");
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
                    case SIGN_OUT:
                        user = null;
                        break;
                    case SERVER_LIST:
                        oOutputStream.writeUnshared(user.getServersNames());
                        clientRequest = (Request) oInputStream.readObject();
                        if (clientRequest == Request.ENTER_SERVER) {
                            String serverName = (String) oInputStream.readObject(); // getting the server's name from client
                            boolean doesServerExist = user.doesServerExist(serverName);
                            oOutputStream.writeObject(doesServerExist);
                            if (doesServerExist) {
                                new ServerHandler(user.getServer(serverName), socket, this.user).start();
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
                            DataManager.addServer(server); // adding the new server to the Database
                            user.addServer(server); // adding the new server to the user's servers list
                        }
                        break;
                    case ADD_NEW_FRIEND:
                        String username = (String) oInputStream.readObject();
                        boolean doesUserExist = (DataManager.isUserNameDuplicate(username));
                        boolean isSuccessful = true;
                        if (doesUserExist) {
                            if (DataManager.getUser(username) == this.user)
                                isSuccessful = false;
                        }
                        oOutputStream.writeObject(isSuccessful);
                        if (isSuccessful) {
                            DataManager.getUser(username).addFriendshipRequest(new FriendshipRequest(DataManager.getUser(username), this.user));
                        }
                        break;
                    case SHOW_FRIENDSHIP_REQESTS:
                        oOutputStream.writeUnshared(user.getFriendshipRequests());
                        Request request1 = (Request) oInputStream.readObject();
                        if (request1 == Request.ANSWER_REQUEST) {
                            String chosenUsername = (String) oInputStream.readObject();
                            oOutputStream.writeObject(user.doesFriendshipRequestExist(chosenUsername));
                            Request request2 = (Request) oInputStream.readObject();
                            if (request2 != Request.BACK) {
                                FriendshipRequest friendshipRequest = user.getFriendshipRequest(chosenUsername);
                                if (request2 == Request.ACCEPT_REQUEST) {
                                    user.addFriend(friendshipRequest.getSenderUser());
                                    friendshipRequest.getSenderUser().addFriend(this.user);
                                    user.removeFriendshipRequest(friendshipRequest);
                                }
                                user.removeFriendshipRequest(friendshipRequest);
                            }
                        }
                        break;
                    case PV_LIST:
                        oOutputStream.writeUnshared(user.getPvChats());
                        Request request2 = (Request) oInputStream.readObject();
                        if (request2 == Request.ENTER_PV) {
                            String PVName = (String) oInputStream.readObject();
                            PVChat pvChat = user.getPVChat(PVName);
                            boolean doesPVChatExist;
                            if (pvChat == null) {
                                doesPVChatExist = false;
                            } else {
                                doesPVChatExist = true;
                            }
                            oOutputStream.writeObject(doesPVChatExist);
                            if (doesPVChatExist) {
                                oOutputStream.writeObject(pvChat.getPortNumber());
                            }
                        }
                        break;
                    case CREATE_PV_CHAT:
                        oOutputStream.writeObject(user.getFriendsList());
                        Request request3 = (Request) oInputStream.readObject();
                        if (request3 == Request.CONTINUE) {
                            String chatName = (String) oInputStream.readObject();
                            boolean isValid = user.doesFriendExist(chatName);
                            oOutputStream.writeObject(isValid);
                            if (isValid) {
                                PVChat pvchat = new PVChat(this.user, user.getFriend(chatName));
                                user.addPvChat(pvchat);
                                user.getFriend(chatName).addPvChat(pvchat);
                                new Thread(pvchat).start();
                            }
                        }
                        System.out.println("Before breaking the switch!");
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error");
        }
    }
}


