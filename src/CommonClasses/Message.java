package  CommonClasses;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.*;

public abstract class Message implements Serializable {
    
    private User user;
    private LocalDateTime date;
    private int likes = 0;
    private int dislikes = 0;
    private int smiles = 0;
    

    // constructor
    public Message(User user) {
        this.user = user;
        date = LocalDateTime.now();
    }

    public abstract String getText();

    public User getUser() {
        return user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    @Override
    public String toString() {
        return user.toString();
    }
}
