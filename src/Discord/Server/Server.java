package Discord.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.*;

import CommonClasses.Role;
import CommonClasses.User;
import CommonClasses.Channel;


public class Server implements Serializable {
    
    private String name;
    private List<Channel> channels;
    private List<User> users;
    private List<Role> roles;
    private final User creator;


    // constructor
    public Server(String name, User creator) {
        this.name = name;
        this.creator = creator;
        channels = Collections.synchronizedList(new ArrayList<>());
        users = Collections.synchronizedList(new ArrayList<>());
        roles = Collections.synchronizedList(new ArrayList<>());
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
    public void addChannel(Channel channel) {
        channels.add(channel);
    }


    public void runChannels() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (Channel channel : channels) {
            executorService.execute(channel);
        }
    }
    public String getName() {
        return name;
    }


    public ArrayList<String> getChannelsNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Channel channel : channels) {
            names.add(channel.getName());
        }
        return names;
    }


    public boolean doesChannelExist(String channelName) {
        for (Channel channel : channels) {
            if (channel.getName().equalsIgnoreCase(channelName)) {
                return true;
            }
        }
        return false;
    }

    public int getChannelPortNumber(String channelName) {
        for (Channel channel : channels) {
            if (channel.getName().equalsIgnoreCase(channelName)) {
                return  channel.getServerPortNumber();
            }
        }
        return -1;
    }

    public boolean isChannelNameDuplicated(String channelName) {
        for (Channel channel : channels) {
            if (channel.getName().equalsIgnoreCase(channelName)) {
                return true;
            }
        }
        return false;
    }

    public User getCreator() {
        return creator;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public ArrayList<String> getUsersNames() {
        ArrayList<String> usersNames = new ArrayList<>();
        for (User user : users) {
            usersNames.add(user.getUsername());
        }
        return usersNames;
    }
}
