package  CommonClasses;

import Discord.Server.*;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable{

    private String username;
    private String password;
    private String email;
    private String phoneNumber; // optional
    private String status;
    private ArrayList<Server> servers;
    private ArrayList<User> friends;
    private List<FriendshipRequest> friendshipRequests;

    
    // constructor
    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        friends = new ArrayList<>();
        servers = new ArrayList<>();
        friendshipRequests = Collections.synchronizedList(new ArrayList<>());
    }


    /**
     * This method is for showing the list of the friends of the user.
     */
    public void showFriends() {
        for (User user : friends) {
            System.out.println("- " + user.getUsername());
        }
    }


    public void addServer(Server server) {
        servers.add(server);
    } 


    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getUsername() {
        return username;
    }


    public String getPassword() {
        return password;
    }


    public String getEmail() {
        return email;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }


    public ArrayList<Server> getServers() {
        return servers;
    }


    public ArrayList<String> getServersNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Server server : servers) {
            names.add(server.getName());
        }
        return names;
    }

    public boolean doesServerExist(String serverName) {
        for (Server server : servers) {
            if (server.getName().equalsIgnoreCase(serverName)) {
                return true;
            }
        }
        return false;
    }

    public boolean doesFriendExist(String username) {
        for (User friend : friends) {
            if (friend.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public User getFriend(String username) {
        for (User user : friends) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }


    public Server getServer(String serverName) {
        for (Server server : servers) {
            if (server.getName().equalsIgnoreCase(serverName)) {
                return server;
            }
        }
        return null;
    }


    public ArrayList<User> getFriends() {
        return friends;
    }

    public void addFriendshipRequest(FriendshipRequest friendshipRequest) {
        friendshipRequests.add(friendshipRequest);
        System.out.println("There is a friendship request :))");
    }

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public boolean doesFriendshipRequestExist(String username) {
        for (FriendshipRequest friendshipRequest : friendshipRequests) {
            if (friendshipRequest.getSenderUser().getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public FriendshipRequest getFriendshipRequest(String username) {
        for (FriendshipRequest friendshipRequest : friendshipRequests) {
            if (friendshipRequest.getSenderUser().getUsername().equalsIgnoreCase(username)) {
                return friendshipRequest;
            }
        }
        return null;
    }

    public void removeFriendshipRequest(FriendshipRequest friendshipRequest) {
        friendshipRequests.remove(friendshipRequest);
    }

    public List<FriendshipRequest> getFriendshipRequests() {
        return friendshipRequests;
    }
}