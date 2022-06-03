package  Discord.Client;

import java.io.Console;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import CommonClasses.*;

/**
 * This class is for handling the actions of the user.
 */
public class ClientHandler {

    private User user;
    private Socket socket;
    private ObjectInputStream oInputStream;
    private ObjectOutputStream oOutputStream;
    private Scanner scanner = new Scanner(System.in);
    private boolean isUserEntered;

    // constructor
    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.isUserEntered = false;
        try {
            oOutputStream = new ObjectOutputStream(socket.getOutputStream());
            oInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("There is a problem in opening the streams!");
            e.printStackTrace();
        }
    }


    /**
     * This method is for printing the menu and managing the choices of the user
     * and calling the proper methods.
     */
    public void start() {
        while (true) {
            MenuPrinter.printFirstMenu();
            try {
                int choice = scanner.nextInt();
                if (choice < 1 || choice > 3) {
                    System.out.println("Your input is invalid!");
                    continue;
                }
                switch (choice) {
                    case 1:
                        scanner.nextLine();
                        isUserEntered = enterAccount(true);
                        break;
                    case 2:
                        scanner.nextLine();
                        isUserEntered = enterAccount(false);
                        break;
                    case 3:
                        isUserEntered = false;
                        return;
                }
                if (isUserEntered) {
                    secondMenuHandler();
                }
            } catch (InputMismatchException e) {
                System.out.println("Your input is invalid!");
                scanner.nextLine();
                continue;
            } catch (IOException e) {
                System.out.println("There is a IO problem!");
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
            }
        }   
    }
    

    /**
     * This method is for handling second menu and using the proper classes for user's choices.
     */
    private void secondMenuHandler() {
        while (true) {
            MenuPrinter.printSecondMenu();
            try {
                int choice = scanner.nextInt();
                if (choice > 5 || choice < 0) {
                    System.out.println("Your input is invalid!");
                    continue;
                }
                switch (choice) {
                    case 1:
                        showServers();
                        break;
                    case 2:
                        createServer();
                        break;
                    case 3:
                        showPVChats();
                        break;
                    case 4:
                        showFriendshipRequests();
                        break;
                    case 5:
                        oOutputStream.writeObject(Request.SIGN_OUT);
                        isUserEntered = false;
                        user = null;
                        return;
                }
            } catch (InputMismatchException e) {
                System.out.println("Your input is invalid!");
                scanner.nextLine();
                continue;
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }


    /**
     * This method shows the friendship requests which are sent to a user. Then, the user can exit, or can accept or reject 
     * the request.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void showFriendshipRequests() throws IOException, ClassNotFoundException {
        oOutputStream.writeObject(Request.SHOW_FRIENDSHIP_REQESTS);
        ArrayList<FriendshipRequest> friendshipRequests = (ArrayList<FriendshipRequest>) oInputStream.readObject();
        if (friendshipRequests.size() == 0) {
            System.out.println("There is no friendship request for you now!");
            oOutputStream.writeObject(Request.BACK);
            return;
        }
        int counter = 0;
        for (FriendshipRequest friendshipRequest : friendshipRequests) {
            System.out.println(counter + "" + friendshipRequest);
        }
        System.out.println("Enter the username of who has sent friendship request to you. if you want to back, enter -1:\n> ");
        String choice = scanner.nextLine();
        if (choice.equals("-1")) {
            oOutputStream.writeObject(Request.BACK);
            return;
        }
        oOutputStream.writeObject(choice);
        while (true) {
            System.out.println("1- Accept    2- Reject");
            try {
                int choice2 = scanner.nextInt();
                if (choice2 == 1) {
                    oOutputStream.writeObject(Request.ACCEPT_REQUEST);
                    return;
                } else if (choice2 == 2) {
                    oOutputStream.writeObject(Request.REJECT_REQUEST);
                    return;
                } else {
                    System.out.println("Your input is invalid!");
                    continue;
                }
            } catch (InputMismatchException e) {
                System.out.println("Your input is not valid!");
                continue;
            }
        }
    }

    /**
     * This method shows the list of PV chats of the user. Then user can choose of the servers or exit.
     */
    private void showPVChats() {
        try {
            oOutputStream.writeObject(Request.PV_LIST);
            ArrayList<String> PVs = (ArrayList<String>) oInputStream.readObject();
            if (PVs.size() == 0) {
                System.out.println("There is no PV chat in your account yet!");
                oOutputStream.writeObject(Request.BACK);
                return;
            }
            for (String PV : PVs) {
                System.out.println("Enter the name of the server you want to enter. If you want back, enter -1\n> ");
                String PVName = scanner.nextLine();
                if (PVName.equals("-1")) {
                    oOutputStream.writeObject(Request.BACK);
                    return;
                }
                oOutputStream.writeObject(Request.ENTER_PV); // sending request
                oOutputStream.writeObject(PVName); // sending server name
                boolean isSuccessful = (boolean) oInputStream.readObject();
                if (isSuccessful) {
                    PVChat pvChat = (PVChat) oInputStream.readObject();
                    int portNumber = (Integer) oInputStream.readObject(); // getting the port number of the server from the server
                    PVHandler pvHandler = new PVHandler(pvChat, portNumber);
                    pvHandler.start();   
                } 
            }   
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    
    /**
     * This method shows the list of server of one user. then user can choose one of the servers or exit.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void showServers() {
        try {
            oOutputStream.writeObject(Request.SERVER_LIST);
            ArrayList<String> servers = (ArrayList<String>) oInputStream.readObject();
            if (servers.size() == 0) {
                System.out.println("You have no server server in your account yet!");
                oOutputStream.writeObject(Request.BACK);
                return;
            }
            // showing the list of the servers
            for (String string : servers) {
                System.out.println("- " + string);
            }
            scanner.nextLine();
            System.out.print("Enter the name of the server you want to enter. If you want back, enter -1\n> ");
            String serverName = scanner.nextLine();
            if (serverName.equals("-1")) {
                oOutputStream.writeObject(Request.BACK);
                return;
            }
            oOutputStream.writeObject(Request.ENTER_SERVER); // sending request
            oOutputStream.writeObject(serverName); // sending server name
            boolean isSuccessful = (boolean) oInputStream.readObject();
            if (isSuccessful) {
                int portNumber = (Integer) oInputStream.readObject(); // getting the port numebr of the server from the server
                ServerHandler serverHandler = new ServerHandler(user, serverName, portNumber);
                System.out.println("After creating ServerHandler!");
                serverHandler.start();
            } else {
                System.out.println("There is no server with this name!");
            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void createServer() throws ClassNotFoundException, IOException {
        scanner.nextLine();
        while (true) {
            System.out.println("Enter the name of the server you want to create.");
            System.out.print("If you want to back, enter -1:\n> ");
            String serverName = scanner.nextLine();
            if (serverName.equals("-1")) {
                return;
            }
            oOutputStream.writeObject(Request.CHECK_SERVER_NAME);
            oOutputStream.writeObject(serverName);
            boolean isDuplicated = (Boolean) oInputStream.readObject();
            if (!isDuplicated) {
                oOutputStream.writeObject(Request.CREATE_SERVER);
                oOutputStream.writeObject(user);
                System.out.println("Now the server is created!");
                break;
            } else {
                oOutputStream.writeObject(Request.BACK);
                continue;
            }
        }
    }

    /**
     * This method gets username and password from the user and then checks if the user is valid or not.
     * It is used for both Sign in and Sign Up. It uses regex to check the validity of the pattern of the strings.
     * @return 0: successful   -1: login is not successful   -2: The information already exists    -3: exit
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private int sendFirstMap(boolean isLogin) throws IOException, ClassNotFoundException {
        String username = null;
        String password = null;
        while (true) {
            System.out.print("Enter your username. if you want to back, enter -1:\n>  ");
            username = scanner.nextLine();
            if (username.equals("-1")) {
                return -3;
            }
            if (Regex.isUsernameValid(username)) {
                break;
            }
            System.out.println("Your username is not valid. The username should be a 6 character combination of alphabets and numbers.");
        }
        while (true) {
            System.out.print("Enter your password:\n> ");
            password = scanner.nextLine();
            if (Regex.isPassValid(password)) {
                break;
            }
            System.out.println("Your password is not valid. The password should be a 8 character string combination of uppercase, lowercase and digits.");
        }
        Map<String, String> userMap = new HashMap<>();
        userMap.put("userName", username);
        userMap.put("passWord", password);
        oOutputStream.writeObject(Request.CHECK_USERNAME);
        oOutputStream.writeObject(userMap); // sending the Map to the server to check the validity
        boolean isUserNameDuplicate = (Boolean) oInputStream.readObject(); // reading the request's answer from the server
        if (isUserNameDuplicate) {
            if (isLogin) {
                oOutputStream.writeObject(Request.SIGN_IN);
                oOutputStream.writeObject(userMap);
                this.user = (User) oInputStream.readObject(); // setting the user field
                return 0;
            } else { // for sign up
                return -2;
            }
        } else {
            if (isLogin) {
                return -1;
            } else {
                user = new User(username, password, null, null);
                return 0;
            }
        }
    }


    /**
     * This method is used when the user wants to Sign up. It gets the email and the phoneNumber from the user and 
     * sends them in a hashmap with the previous information: username and password.
     * @return 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private boolean sendSecondMap() throws IOException, ClassNotFoundException {
        String email = null;
        String phoneNumber = null;
        while (true) {
            System.out.print("Enter your email:\n> ");
            email = scanner.nextLine();
            if (Regex.isEmailValid(email)) {
                break;
            }
            System.out.println("Your email is not valid. Try again!");
        }
        while (true) {
            System.out.print("Enter your phone number. If you don't want, enter -1:\n> ");
            phoneNumber = scanner.nextLine();
            if (phoneNumber.equals("-1")) {
                phoneNumber = null;
                break;
            } else {
                if (Regex.isNumberValid(phoneNumber)) {
                    break;
                }
                System.out.println("Your phone number is not valid. Try again!");
            }
        }
        oOutputStream.writeObject(Request.SIGN_UP);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        oOutputStream.writeObject(user);
        return (boolean) oInputStream.readObject();
    }


    /**
     * This method is for both Sign up and Sign in. It calls the proper methods for each one.
     * It returns false if user can't enter his account, and returns true if the user entered the account successfuly.
     * @param isLogin 
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private boolean enterAccount(boolean isLogin) throws ClassNotFoundException, IOException {
        while (true) {
            int result = sendFirstMap(isLogin);
            if (result == -3) {
                return false;
            } 
            if (isLogin) { 
                if (result == -1) {
                    System.out.println("Your username or password is not correct. Try again!");
                    continue;
                } else {
                    System.out.println("You logged in succesfully!");
                    return true;
                }
            } else {
                if (result == 0) {
                    boolean isSignUpSuccessful = sendSecondMap();
                    if (isSignUpSuccessful) {
                        System.out.println("You signed up successfully!");
                        return true;
                    } else {
                        System.out.println("Our server had problems signing you up!");
                        return false;
                    }
                } else {
                    System.out.println("You have logged in before. You can login now!");
                    return false;
                }
            }
        }
    }
}
