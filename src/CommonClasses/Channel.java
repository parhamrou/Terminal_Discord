package  CommonClasses;

import java.io.*;
import java.util.ArrayList;

public abstract class Channel implements Serializable {
    
    private String name;
    private ArrayList<User> users;
    private ArrayList<Message> messages;
    private ArrayList<Message> pinnedMessages;
    
    // constructor
    public Channel(String name) {
        this.name = name;
        users = new ArrayList<>();
        messages = new ArrayList<>();
        pinnedMessages = new ArrayList<>();
    }


    public ArrayList<Message> getMessages() {
        return messages;
    }


    public ArrayList<Message> getPinnedMessages() {
        return pinnedMessages;
    }


    public String getName() {
        return name;
    }
}
