package Discord.Server;

import java.util.*;
import java.io.*;
import CommonClasses.*;

// this class if for server side.

public class AccountManager {
    private final static String DBAddress = "userInfo.DAT";

    public synchronized static ArrayList<User> readDB(String fileAddress) {
        ArrayList<User> users = new ArrayList<>();

        try {
            FileInputStream fInputStream = new FileInputStream(fileAddress);
            while (true) {
                ObjectInputStream objectInputStream = new ObjectInputStream(fInputStream);
                User tempUser = (User) objectInputStream.readObject();
                if (tempUser == null) {
                    break;
                }

                users.add(tempUser);
            }
        } catch (IOException e) {
            return users;
        } catch (ClassNotFoundException e) {
            System.out.println("Error reading DB!");
        }
        return users;
    }

    public synchronized static boolean writeDB(String fileAddress, User user) {
        try (
            ObjectOutputStream oOutputStream = new ObjectOutputStream(new FileOutputStream(fileAddress, true))){
            oOutputStream.writeObject(user);
            return true;
        } catch (IOException e) {
            System.out.println("ERROR IN WRITING");
            return false;
        }
    }


    public static boolean isUserNameDuplicate(String userName) {
        ArrayList<User> userList = readDB(DBAddress);
        for (User user : userList) {
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
        return writeDB(DBAddress, newAccount);
    }

    public static User signIn(HashMap<String, String> account) {
        String userName = account.get("userName");
        String passWord = account.get("passWord");

        ArrayList<User> userList = readDB(DBAddress);

        for (User user: userList) {
            if (userName.equalsIgnoreCase(user.getUsername()) && passWord.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }
}
