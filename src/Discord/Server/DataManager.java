package Discord.Server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import CommonClasses.*;

/**
 * This class loads data from the files just after the server starts to work.
 * It keeps data in different lists.
 */
public class DataManager {

    private static final String usersFileAddress = "D:\\APFinalProject\\DataFiles\\uesrs.DAT";
    private static final String serversFileAddress = "D:\\APFinalProject\\DataFiles\\servers.DAT";
    
    private static List<User> users = Collections.synchronizedList(new ArrayList<>());
    private static List<Server> servers = Collections.synchronizedList(new ArrayList<>());

    private static FileInputStream fInputStream;
    private static ObjectInputStream objectInputStream;

    /**
     * This \method calls the methods which have to load data from multiple files.
     */
    public static void LoadData() {
        try {
            readUsers();
            readServers();
            runServers();
        } catch (FileNotFoundException e) {
            System.out.println("There is no file woth this address!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("There is a I/O problem!");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    private static void readUsers() throws ClassNotFoundException, IOException {
        fInputStream = new FileInputStream(usersFileAddress);
        objectInputStream = new ObjectInputStream(fInputStream);
        while (true) {
            User tempUser = (User) objectInputStream.readObject();
            if (tempUser == null) {
                break;
            }
            users.add(tempUser);
        }
        objectInputStream.close();
        fInputStream.close();
    }


    private static void readServers() throws IOException, ClassNotFoundException {
        fInputStream = new FileInputStream(serversFileAddress);
        objectInputStream = new ObjectInputStream(fInputStream);
        while (true) {
            Server tempServer = (Server) objectInputStream.readObject();
            if (tempServer == null) {
                break;
            }
            servers.add(tempServer);
        }
        objectInputStream.close();
        fInputStream.close();
    }

    private static void runServers() {
        for (Server server : servers) {
            server.start();
            // must run the channels either.
        }
    }

    public static boolean isServerNameDuplicated(String serverName) {
        for (Server server : servers) {
            if (server.getName().equalsIgnoreCase(serverName)) {
                return  true;
            }
        }
        return  false;
    }


    public static boolean isUserNameDuplicate(String userName) {
        for (User user : users) {
            if (userName.equalsIgnoreCase(user.getUsername())) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkUserName(HashMap<String, String> newAccount) {
        String userName = newAccount.get("userName");
        return isUserNameDuplicate(userName);
    }

    public synchronized static boolean signUp(User newAccount) {
        return addUser(newAccount);
    }


    public static User signIn(HashMap<String, String> account) {
        String userName = account.get("userName");
        String passWord = account.get("passWord");
        for (User user: users) {
            if (userName.equalsIgnoreCase(user.getUsername()) && passWord.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    public static List<Server> getServers() {
        return servers;
    }


    public static List<User> getUsers() {
        return users;
    }

    private static  boolean addUser(User user) {
        users.add(user);
        return true;
    }

    public static void addServer(Server server) {
        servers.add(server);
    }

    
}
