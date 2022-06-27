package  Discord.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.*;

import CommonClasses.*;

/**
 * This class has the responsibility to make the connection between one server and the client.
 * It gets the client's requests and passes them to the server's 'ServerHandler' class.
 */
public class ServerHandler {

    private String serverName;
    private User user;
    private Socket socket;
    private Scanner scanner = new Scanner(System.in);
    private ObjectInputStream oInputStream;
    private ObjectOutputStream outputStream;

    // constructor
    public ServerHandler(User user ,String serverName, Socket socket) {
        try {
            System.out.println("Now a server Handler is created!");
            this.user = user;
            this.serverName = serverName;
            this.socket = socket;
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            oInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    /**
     * This method is called just after a user chooses one server to work with.
     * It gets the user's choice and calls proper methods.
     */
    public void start() {
        while (true) {
            MenuPrinter.serverMenu();
            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        showChannels();
                        break;
                    case 2:
                        createChannel();
                        break;
                    case 3:
                        addRole();
                        break;
                    case 4:
                        addUser();
                        break;
                    case 5:
                        removeUser();
                        break;
                    case 6:
                        oInputStream.close();
                        outputStream.close();
                        socket.close();
                        return;
                    default:
                        System.out.println("You input is invalid!");
                        break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Your input is invalid!");
                scanner.nextLine();
                continue;
            } catch (ClassNotFoundException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }
    }

    private void showChannels() throws IOException, ClassNotFoundException {
        try {
            outputStream.writeObject(Request.CHANNEL_LIST);
            ArrayList<String> channels = (ArrayList<String>) oInputStream.readObject();
            if (channels.size() == 0) {
                System.out.println("There is not channel in this server yet!");
                outputStream.writeObject(Request.BACK);
                return;
            }
            // showing the list of the servers
            for (String string : channels) {
                System.out.println("- " + string);
            }
            scanner.nextLine();
            System.out.print("Enter the name of the channel you want to enter. If you want back, enter -1\n> ");
            String channelName = scanner.nextLine();
            if (serverName.equals("-1")) {
                outputStream.writeObject(Request.BACK);
                return;
            }
            outputStream.writeObject(Request.ENTER_CHANNEL); // sending request
            outputStream.writeObject(channelName); // sending server name
            boolean isSuccessful = (boolean) oInputStream.readObject();
            if (isSuccessful) {
                int portNumber = (Integer) oInputStream.readObject(); // getting the port numebr of the server from the server
                new ChannelHandler(portNumber, user).start();
            } 
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void createChannel() throws ClassNotFoundException, IOException {
        scanner.nextLine();
        while (true) {
            System.out.println("Enter the name of the channel you want to create.");
            System.out.print("If you want to back, enter -1:\n> ");
            String channelName = scanner.nextLine();
            if (channelName.equals("-1")) {
                return;
            }
            outputStream.writeObject(Request.CHECK_CHANNEL_NAME);
            outputStream.writeObject(channelName);
            boolean isDuplicated = (Boolean) oInputStream.readObject();
            if (!isDuplicated) {
                outputStream.writeObject(Request.CREATE_CHANNEL);
                System.out.println("Now the channel is created!");
                break;
            } else {
                outputStream.writeObject(Request.BACK);
                continue;
            }
        }
    }

    private void addRole() throws IOException, ClassNotFoundException {
        outputStream.writeObject(Request.ADD_ROLE);
        outputStream.writeObject(user.getUsername());
        boolean isCreator = (boolean) oInputStream.readObject();
        if (!isCreator) {
            System.out.println("You have to be the server's creator to add a new role!");
            outputStream.writeObject(Request.BACK);
            return;
        }
        outputStream.writeObject(Request.CREATE_ROLE);
        outputStream.writeObject(createRole());
    }


    private Role createRole() {
        System.out.println("For each question, Enter '0' for 'NO' or '1' for 'YES' in one line:");
        System.out.println("1- Can create a channel? ");
        System.out.println("2- Can remove a channel?");
        System.out.println("3- Can remove a user from server?");
        System.out.println("4- blah blah :))");
        System.out.println("5- blah blah 2");
        System.out.println("6- Can change the name of the server? ");
        System.out.println("7- Can see the chat's history?");
        System.out.println("8- Can pin a message? ");
        boolean[] choices = new boolean[8];
        for (int i = 0; i < 8; i++) {
            while (true) {
                try {
                    int choice = scanner.nextInt();
                    if (choice != 1 && choice != 0) {
                        System.out.println("You input is invalid!");
                        continue;
                    }
                    if (choice == 1) {
                        choices[i] = true;
                    } else {
                        choices[i] = false;
                    }
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Your input is invalid!");
                    continue;
                }
            }
        }
        return new Role(choices);
    }

    private void addUser() throws IOException, ClassNotFoundException {
        scanner.nextLine();
        outputStream.writeObject(Request.ADD_USER);
        List<User> friends = (List<User>) oInputStream.readObject();
        if (friends.size() == 0) {
            System.out.println("You have no friends yet!");
            outputStream.writeObject(Request.BACK);
            return;
        }
        System.out.println("Here is your friends' list: ");
        int counter = 1;
        for (User user : friends) {
            System.out.println(counter + "- " + user.getUsername());
            counter++;
        }
        System.out.println("Enter the username of the user you want to add to the server. if You want to back, enter -1: ");
        String username = scanner.nextLine();
        if (username.equals("-1")) {
            outputStream.writeObject(Request.BACK);
            return;
        }
        outputStream.writeObject(Request.CONTINUE);
        outputStream.writeObject(username);
        boolean isUsernameValid = (boolean) oInputStream.readObject();
        if (!isUsernameValid) {
            System.out.println("There is no user with this username!");
            return;
        } else {
            System.out.println("The user is added to server!");
        }
    }

    private void removeUser() throws IOException, ClassNotFoundException {
        outputStream.writeObject(Request.REMOVE_USER);
        ArrayList<String> usersName = (ArrayList<String>) oInputStream.readObject();
        // must be continued from here.
    }
}
