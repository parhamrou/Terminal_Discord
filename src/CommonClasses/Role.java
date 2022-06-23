package  CommonClasses;

import java.io.*;

public class Role implements Serializable {

    private String name;
    // capabalities
    private boolean canCreateChannel;
    private boolean canRemoveChannel;
    private boolean canRemoveUser;
    private boolean canBanFromChannel;
    private boolean canBanFromServer;
    private boolean canChangeName;
    private boolean canSeeHistory;
    private boolean canPin;

    
    // constructor
    public Role(boolean[] answers) {
        this.canCreateChannel = answers[0];
        this.canRemoveChannel = answers[1];
        this.canRemoveUser = answers[2];
        this.canBanFromChannel = answers[3];
        this.canBanFromServer = answers[4];
        this.canChangeName = answers[5];
        this.canSeeHistory = answers[6];
        this.canPin = answers[7];
    }
}
