package Discord.Server;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

import CommonClasses.Role;
import CommonClasses.User;


public class Server implements Serializable {
    
    private String name;
    private List<TextChannel> channels;
    private List<User> users;
    private List<Role> roles;
    private final User creator;
    private HashMap<User, Role> roleMap;

    // constructor
    public Server(String name, User creator) {
        this.name = name;
        this.creator = creator;
        roleMap = new HashMap<>();
        channels = Collections.synchronizedList(new ArrayList<>());
        users = Collections.synchronizedList(new ArrayList<>());
        roles = Collections.synchronizedList(new ArrayList<>());
        boolean[] boss = {true, true, true, true, true, true, true, true};
        roleMap.put(creator, new Role("BOSS", boss)); // creator has BOSS ROLE
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
     */
    public synchronized void removeUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                users.remove(user);
                break;
            }
        }
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
    public void addChannel(TextChannel channel) {
        channels.add(channel);
    }


    public void runChannels() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (TextChannel channel : channels) {
            executorService.execute(channel);
        }
    }
    public String getName() {
        return name;
    }


    public ArrayList<String> getChannelsNames() {
        ArrayList<String> names = new ArrayList<>();
        for (TextChannel channel : channels) {
            names.add(channel.getChannelName());
        }
        return names;
    }


    public boolean doesChannelExist(String channelName) {
        for (TextChannel channel : channels) {
            if (channel.getChannelName().equalsIgnoreCase(channelName)) {
                return true;
            }
        }
        return false;
    }

    public int getChannelPortNumber(String channelName) {
        for (TextChannel channel : channels) {
            if (channel.getChannelName().equalsIgnoreCase(channelName)) {
                return  channel.getPortNumber();
            }
        }
        return -1;
    }

    public boolean isChannelNameDuplicated(String channelName) {
        for (TextChannel channel : channels) {
            if (channel.getChannelName().equalsIgnoreCase(channelName)) {
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

    public void mapRole(User user, Role role) {
        roleMap.put(user, role);
    }

    public ArrayList<String> roleNames() {
        ArrayList<String> roleNames = new ArrayList<>();
        for (Role role : roles) {
            roleNames.add(role.getName());
        }
        return roleNames;
    }

    public Role getRole(String roleName) {
        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        return null;
    }

    public User getUser(String username) {
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }


    public void removeServerFromUsers() {
        for (User user : users) {
            user.removeServer(this);
        }
    }

    public HashMap<User, Role> getRoleMap() {
        return roleMap;
    }
}
