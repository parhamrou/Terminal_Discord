package Discord.Server;

import CommonClasses.*;

import javax.xml.crypto.*;
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
                        if (clientRequest == Request.ENTER_CHAT) {
                            String channelName = (String) objectInputStream.readObject();
                            boolean doesChannelExist = server.doesChannelExist(channelName);
                            objectOutputStream.writeObject(doesChannelExist);
                            if (doesChannelExist) {
                                objectOutputStream.writeObject(server.getChannelPortNumber(channelName)); // sending the channel's port number
                            }
                        }
                        break;
                    case CHECK_CHANNEL_NAME:
                        boolean canCreateChannel = server.getRoleMap().get(user).CanCreateChannel();
                        objectOutputStream.writeObject(canCreateChannel);
                        if (!canCreateChannel) {
                            break;
                        }
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
                        boolean isCreator = (server.getCreator() == this.user);
                        objectOutputStream.writeObject(isCreator);
                        if ((Request) objectInputStream.readObject() == Request.CREATE_ROLE) {
                            server.addRole((Role) objectInputStream.readObject());
                            System.out.println("The new role is added!");
                        }
                        break;
                    case MAP_ROLE:
                        isCreator = (server.getCreator() == this.user);
                        objectOutputStream.writeObject(isCreator);
                        if (!isCreator) {
                            break;
                        }
                        objectOutputStream.writeUnshared(server.roleNames());
                        if ((Request) objectInputStream.readObject() == Request.CHECK_ROLE_NAME) {
                            String roleName = (String) objectInputStream.readObject();
                            Role role = server.getRole(roleName);
                            if (role == null) {
                                objectOutputStream.writeObject(false);
                            } else {
                                objectOutputStream.writeObject(true);
                                objectOutputStream.writeUnshared(server.getUsersNames());
                                if ((Request) objectInputStream.readObject() == Request.CHECK_USERNAME) {
                                    String username = (String) objectInputStream.readObject();
                                    User user = server.getUser(username);
                                    if (user == null) {
                                        objectOutputStream.writeObject(false);
                                    } else {
                                        objectOutputStream.writeObject(true);
                                        server.mapRole(user, role);
                                    }
                                }
                            }
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
                        boolean canRemove;
                        if (!server.getRoleMap().containsKey(user)) {
                            canRemove = false;
                        } else {
                            canRemove = server.getRoleMap().get(user).CanRemoveUser();
                        }
                        objectOutputStream.writeObject(canRemove);
                        if (!canRemove) {
                            break;
                        }
                        objectOutputStream.writeUnshared(server.getUsersNames());
                        if ((Request) objectInputStream.readObject() == Request.CHECK_USERNAME) {
                            String username = (String) objectInputStream.readObject();
                            User user = server.getUser(username);
                            if (user == null) {
                                objectOutputStream.writeObject(false);
                            } else {
                                objectOutputStream.writeObject(true);
                                server.removeUser(username);
                            }
                        }
                        break;
                    case DELETE_SERVER:
                        isCreator = (server.getCreator() == this.user);
                        objectOutputStream.writeObject(isCreator);
                        if (!isCreator) {
                            break;
                        }
                        server.removeServerFromUsers(); // removing from users' lists
                        DataManager.removeServer(server);
                        return;
                    case BACK:
                        return;
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
