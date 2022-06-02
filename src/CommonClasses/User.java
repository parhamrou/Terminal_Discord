package  CommonClasses;

import java.io.Serializable;
import java.util.ArrayList;

import Discord.Server.Server;

public class User implements Serializable{

    private String username;
    private String password;
    private String email;
    private String phoneNumber; // optional
    private String status;
    private ArrayList<Server> servers;
    private ArrayList<User> friends;

    
    // constructor
    public User(String username, String password, String email, String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        friends = new ArrayList<>();
        servers = new ArrayList<>();
    }


    /**
     * This method is for showing the list of the friends of the user.
     */
    public void showFriends() {

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

    public int getServerPort(String serverName) {
        for (Server server : servers) {
            if (server.getName().equalsIgnoreCase(serverName)) {
                return server.getServerPort();
            }
        }
        return -1;
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
            if (server.getName().equals(serverName)) {
                return true;
            }
        }
        return false;
    }
}