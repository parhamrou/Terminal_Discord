package  Discord.Client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import CommonClasses.*;

public class ServerHandler implements Serializable {

    private String serverName;
    private Socket socket;
    private Scanner scanner = new Scanner(System.in);
    private ObjectInputStream oInputStream;
    private ObjectOutputStream outputStream;

    // constructor
    public ServerHandler(String serverName, int portNumber) {
        try {
            this.serverName = serverName;
            socket = new Socket("localhost", portNumber);
            oInputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
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
            } catch (InputMismatchException e) {
                System.out.println("Your input is invalid!");
                continue;
            }
        }
    }

    private void showChannels() throws IOException, ClassNotFoundException {
        outputStream.writeObject(Request.SHOW_CHANNELS);
        ArrayList<String> channels = (ArrayList<String>) oInputStream.readObject();
        for (String string : channels) {
            System.out.println(string);
        }
        System.out.print("Enter the name of the channel you want to enter, ");
        System.out.println("Or enter -1 to exit\n> ");
        try {
            String choice = scanner.nextLine();
            if (choice.equals("-1")) {
                return;
            }
            outputStream.writeObject(Request.ENTER_CHANNEL);
            boolean isSuccessful = (boolean) oInputStream.readObject();
            if (isSuccessful) {

            }
        } catch (InputMismatchException e) {
            System.out.println(e);
        }
    }
}
