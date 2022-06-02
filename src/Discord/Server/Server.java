package Discord.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.util.ArrayList;

import CommonClasses.Role;
import CommonClasses.User;
import Discord.Server.*;

public class Server implements Runnable, Serializable {
    
    private String name;
    private ArrayList<Channel> channels;
    private ArrayList<User> users;
    private ArrayList<Role> roles;
    private final User creator;
    private int serverPortNumber;

    // constructor
    public Server(String name, User creator) {
        this.name = name;
        this.creator = creator;
        channels = new ArrayList<>();
        users = new ArrayList<>();
        roles = new ArrayList<>();
    }

    @Override
    public void run() {
        Socket socket;
        System.out.println("Now the server '" + name + "' is listening...");
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            serverPortNumber = serverSocket.getLocalPort();
            // waiting for a client to connect and pass it to a new thread.
            while (true)  {
                try {
                    socket = serverSocket.accept();
                    System.out.println("Now a client is connected to the server '" + name + "' with port number: " + socket.getPort());
                    ServerHandler serverHandler = new ServerHandler(this, socket);
                    new Thread(serverHandler).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    // for changing the name of the server
    public void setName(String name) {
        this.name = name;
    }


    /**
     * This method is for adding a new user to the server.
     * @param user
     */
    public void addUser(User user) {
        users.add(user);
        // printing the welcome message
    }


    /**
     * This method is for removing a user from the server.
     * @param user
     */
    public void removeUser(User user) {
        users.remove(user);
    }

    
    /**
     * This method is for creating a new role in the server
     */
    public void AddRole(Role role) {
        roles.add(role);
    }


    /**
     * This method is for adding a new channel in the server.
     */
    public void createChannel(Channel channel) {
        channels.add(channel);
    }

    public String getName() {
        return name;
    }

    public int getServerPort() {
        return serverPortNumber;
    }
}
