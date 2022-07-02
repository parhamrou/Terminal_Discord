package  Discord.Server;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import CommonClasses.*;

public class ClientHandler implements Runnable {

    
    private ObjectInputStream oInputStream;
    private ObjectOutputStream oOutputStream;
    private final Socket socket;
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
                        getServerList();
                        break;
                    case CHECK_SERVER_NAME:
                        createServer();
                        break;
                    case ADD_NEW_FRIEND:
                        addNewFriend();
                        break;
                    case SHOW_FRIENDSHIP_REQESTS:
                        answerFriendshipRequest();
                        break;
                    case PV_LIST:
                        enterPV();
                        break;
                    case CREATE_PV_CHAT:
                        createPVChat();
                        break;
                    case EXIT:
                        socket.close();
                        return;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error");
        }
    }


    private void getServerList() throws IOException, ClassNotFoundException {
        oOutputStream.writeUnshared(user.getServersNames());
        Request clientRequest = (Request) oInputStream.readObject();
        if (clientRequest == Request.ENTER_SERVER) {
            String serverName = (String) oInputStream.readObject(); // getting the server's name from client
            boolean doesServerExist = user.doesServerExist(serverName);
            oOutputStream.writeObject(doesServerExist);
            if (doesServerExist) {
                new ServerHandler(user.getServer(serverName), socket, this.user).start();
            }
        }
    }

    private void createServer() throws IOException, ClassNotFoundException {
        String serverName = (String) oInputStream.readObject();
        oOutputStream.writeObject(DataManager.isServerNameDuplicated(serverName));
        Request request = (Request) oInputStream.readObject();
        if (request == Request.CREATE_SERVER) {
            User user = (User) oInputStream.readObject();
            Server server = new Server(serverName, user);
            DataManager.addServer(server); // adding the new server to the Database
            user.addServer(server); // adding the new server to the user's servers list
        }
    }


    private void addNewFriend() throws IOException, ClassNotFoundException {
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
    }

    private void answerFriendshipRequest() throws IOException, ClassNotFoundException {
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
    }


    private void enterPV() throws IOException, ClassNotFoundException {
        oOutputStream.writeUnshared(user.getPvChats());
        Request request2 = (Request) oInputStream.readObject();
        if (request2 == Request.ENTER_CHAT) {
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
    }

    private void createPVChat() throws IOException, ClassNotFoundException {
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
                DataManager.addPVChat(pvchat);
                new Thread(pvchat).start();
            }
        }
    }
}


