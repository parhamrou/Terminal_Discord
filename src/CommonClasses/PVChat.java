package  CommonClasses;

import java.io.File;
import java.util.ArrayList;

public class PVChat {

    private User user1;
    private User user2;
    private ArrayList<Message> messages;
    private ArrayList<File> files;
    private boolean isUser1Blocked = false;
    private boolean isUser2Blocked = false;

    
    // constructor
    public PVChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        files = new ArrayList<>();
        messages = new ArrayList<>();
    }

    public User getUser1() {
        return user1;
    }


    public User getUser2() {
        return user2;
    }
    

    public void block() {
        if (isUser2Blocked) {
            System.out.println("You have blocked this user before.");
            return;
        }
        isUser2Blocked = true;
    }
}
