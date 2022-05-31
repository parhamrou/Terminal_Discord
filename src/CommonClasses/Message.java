package  CommonClasses;

import java.time.LocalDateTime;

public class Message {
    
    private User user;
    private String text;
    private LocalDateTime date;
    private int likes = 0;
    private int dislikes = 0;
    private int smiles = 0;
    

    // constructor
    public Message(User user, String text) {
        this.user = user;
        this.text = text;
        date = LocalDateTime.now();
    }

    public String getText() {
        return text;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s\n", user.getUsername(), text, date);
    }
}
