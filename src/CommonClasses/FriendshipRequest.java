package  CommonClasses;

import java.io.*;
import java.time.LocalDateTime;

public class FriendshipRequest implements Serializable {

    private LocalDateTime date;
    private User destinationUser;


    // constructor
    public FriendshipRequest(User destinationUser) {
        this.destinationUser = destinationUser;
        date = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\n", destinationUser.getUsername(), date);
    }
}
