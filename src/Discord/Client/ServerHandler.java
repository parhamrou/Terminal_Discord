package  Discord.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import CommonClasses.Request;
import CommonClasses.User;

/**
 * This class has the responsibility to make the connection between one server and the client.
 * It gets the client's requests and passes them to the server's 'ServerHandler' class.
 */
public class ServerHandler implements Serializable {

    private String serverName;
    private User user;
    private Socket socket;
    private Scanner scanner = new Scanner(System.in);
    private ObjectInputStream oInputStream;
    private ObjectOutputStream outputStream;

    // constructor
    public ServerHandler(User user ,String serverName, int portNumber) {
        try {
            System.out.println("Now a server Handler is created!");
            this.user = user;
            this.serverName = serverName;
            socket = new Socket("localhost", portNumber);
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
                        // adding user to the server
                        break;
                    case 4:
                        // removing user from the server
                        break;
                    case 5:
                        // creating channel
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
}
