package  Discord.Client;

import CommonClasses.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This class is for handling and showing one channel when a user is working with that.
 * The functions which are used to save the changes in the messages list and more are on Server side.
 */
public class ChannelHandler extends ChatHandler{

    
    // constructor
    public ChannelHandler(int portNumber, User user) {
        super(portNumber, user);
    }

    /**
     * This method runs just after one user wants to enter a channel.
     * It calls the proper methods and then waits for user's choices.
     */
    public void start() {
        while (true) {
            try {
                MenuPrinter.printChannelMenu();
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        loadChat();
                        runThreads();
                        break;
                    case 2:
                        // delete channel;
                        return;
                    case 3:
                        outputStream.writeObject(Request.EXIT);
                        return;
                }
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (InputMismatchException e) {
                System.out.println("You have to enter an integer value!");
                continue;
            }
        }
    }
}
