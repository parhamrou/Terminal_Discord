package  CommonClasses;

import java.time.LocalDateTime;

public class FriendshipRequest {

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
