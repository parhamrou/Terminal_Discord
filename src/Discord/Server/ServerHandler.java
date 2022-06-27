package Discord.Server;

import CommonClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerHandler {

    private Server server;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private User user;

    // constructor
    public ServerHandler(Server server, Socket socket, User user) {
        this.server = server;
        this.socket = socket;
        this.user = user;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public void start() {
        Request clientRequest;
        while (true) {
            try {
                System.out.println("Waiting for request in serverHandler in sevrer side!");
                clientRequest = (Request) objectInputStream.readObject();
                System.out.println("Request: " + clientRequest.toString());
                switch (clientRequest) {
                    case CHANNEL_LIST:
                        objectOutputStream.writeObject(server.getChannelsNames());
                        clientRequest = (Request) objectInputStream.readObject();
                        if (clientRequest == Request.ENTER_CHANNEL) {
                            String channelName = (String) objectInputStream.readObject();
                            boolean doesChannelExist = server.doesChannelExist(channelName);
                            objectOutputStream.writeObject(doesChannelExist);
                            if (doesChannelExist) {
                                objectOutputStream.writeObject(server.getChannelPortNumber(channelName)); // sending the channel's port number
                            }
                        }
                        break;
                    case CHECK_CHANNEL_NAME:
                        String channelName = (String) objectInputStream.readObject();
                        objectOutputStream.writeObject(server.isChannelNameDuplicated(channelName));
                        Request request = (Request) objectInputStream.readObject();
                        if (request == Request.CREATE_CHANNEL) {
                            TextChannel channel = new TextChannel(channelName);
                            server.addChannel(channel); // adding the new server to the Database
                            new Thread(channel).start(); // running the channel
                        }
                        break;
                    case ADD_ROLE:
                        String userName = (String) objectInputStream.readObject();
                        boolean isCreator = (server.getCreator().getUsername().equalsIgnoreCase(userName));
                        objectOutputStream.writeObject(isCreator);
                        if ((Request) objectInputStream.readObject() == Request.CREATE_ROLE) {
                            server.addRole((Role) objectInputStream.readObject());
                            System.out.println("The new role is added!");
                        }
                        break;
                    case ADD_USER:
                        objectOutputStream.writeObject(user.getFriends());
                        Request request1 = (Request) objectInputStream.readObject();
                        if (request1 != Request.BACK) {
                            String username = (String) objectInputStream.readObject();
                            boolean doesFriendExist = user.doesFriendExist(username);
                            objectOutputStream.writeObject(doesFriendExist);
                            if (doesFriendExist) {
                                User friend = user.getFriend(username);
                                server.addUser(friend); // adding user to the server
                                friend.addServer(this.server); // adding server to the user's list
                            }
                        }
                        break;
                    case REMOVE_USER:
                        objectOutputStream.writeObject(server.getUsersNames());
                        // must be continued from here.
                }
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }
}
