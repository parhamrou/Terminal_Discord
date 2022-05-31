package  CommonClasses;

import java.nio.channels.Channel;
import java.util.ArrayList;

public class Server {
    
    private String name;
    private ArrayList<Channel> channels;
    private ArrayList<User> users;
    private ArrayList<Role> roles;
    private final User creator;


    // constructor
    public Server(String name, User creator) {
        this.name = name;
        this.creator = creator;
        channels = new ArrayList<>();
        users = new ArrayList<>();
        roles = new ArrayList<>();
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
     * @param name
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
}
