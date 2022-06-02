package  Discord.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.Channel;
import java.util.ArrayList;

import CommonClasses.Role;
import CommonClasses.User;

public class Server {
    
    private String name;
    private ArrayList<Channel> channels;
    private ArrayList<User> users;
    private ArrayList<Role> roles;
    private ServerSocket serverSocket;
    private final User creator;


    // constructor
    public Server(String name, User creator) {
        this.name = name;
        this.creator = creator;
        channels = new ArrayList<>();
        users = new ArrayList<>();
        roles = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(0);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    public void start() {
        Socket socket;

        // waiting for a client to connect and pass it to a new thread.
        while (true) {
            try {
                socket = serverSocket.accept();
                ServerHandler serverHandler = new ServerHandler(this, socket);
                Thread thread = new Thread(serverHandler);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        return serverSocket.getLocalPort();
    }
}
