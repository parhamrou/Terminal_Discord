package  Discord.Client;

/**
 * This class has some static methods for printing the methods which are used in the program.
 */
public class MenuPrinter {
    
    /**
     * This method prints the first menu in the beginning of the program.
     */
    public static void printFirstMenu() {
        System.out.println("1- Sign in");
        System.out.println("2- Sign up");
        System.out.println("3- Exit");
        System.out.print("> ");
    }


    /**
     * This method prints the second menu in which the user has to choose what action does he wants to do.
     */
    public static void printSecondMenu() {
        System.out.println("1- Show servers");
        System.out.println("2- Create server");
        System.out.println("3- Show Personal chats");
        System.out.println("4- Start a new chat");
        System.out.println("5- friendship requests");
        System.out.println("6- Add new friend");
        System.out.println("7- Sign out");
        System.out.print("> ");
    }


    /**
     * This method prints the channel's options.
     */
    public static void printChannelMenu() {
        System.out.println("1- Enter channel");
        System.out.println("2- Exit");
    }


    public static void PVMenu() {
        System.out.println("1- Enter chat");
        System.out.println("2- Block");
        System.out.println("3- Exit");
        System.out.print("> ");
    }


    public static void serverMenu() {
        // NOTE: WE HAVE TO CHECK IF THE KEY IS VALID IN MAP OR NOT
        System.out.println("1- Show channels");
        System.out.println("2- Create channel");
        System.out.println("3- Delete a channel");
        System.out.println("4- Add new role"); // checked! ✅
        System.out.println("5- Map a role to a user"); // checked! ✅
        System.out.println("6- Add user to server"); // checked! ✅
        System.out.println("7- Remove user from server"); // checked! ✅
        System.out.println("8- Delete this server");
        System.out.println("9- Change the name of the server");
        System.out.println("10- Exit");
        System.out.print("> ");
    }
}
