package  CommonClasses;

public class Role {

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
    public Role(boolean canCreateChannel, boolean canRemoveChannel, boolean canRemoveUser, boolean canBanFromChannel, boolean canBanFromServer, boolean canChangeName, boolean canSeeHistory, boolean canPin) {
        this.canCreateChannel = canBanFromChannel;
        this.canRemoveChannel = canRemoveChannel;
        this.canRemoveUser = canRemoveUser;
        this.canBanFromChannel = canBanFromChannel;
        this.canBanFromServer = canBanFromServer;
        this.canChangeName = canChangeName;
        this.canSeeHistory = canSeeHistory;
        this.canPin = canPin;
    }
}
