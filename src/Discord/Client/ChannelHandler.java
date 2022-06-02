package  Discord.Client;

import CommonClasses.Message;
import CommonClasses.Request;
import CommonClasses.User;
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
public class ChannelHandler {

    private User user;
    private Socket socket;
    private ObjectInputStream oInputStream;
    private ObjectOutputStream outputStream;
    private final Scanner scanner = new Scanner(System.in);

    
    // constructor
    public ChannelHandler(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
        try {
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            oInputStream = new ObjectInputStream(socket.getInputStream());   
        } catch (IOException e) {
            System.out.println("There is a problem in opening the streams!");
            e.printStackTrace();
        }
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
                            loadChannel();
                            runThreads();
                            break;
                        case 2:
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



    /**
     * This method is used for reading messages from the user and printing them in the console.
     * @throws ClassNotFoundException
     */
    private void serverReader() throws ClassNotFoundException {
        try {
            Message message = null;
            while (true) {
                message = (Message) oInputStream.readObject();
                if (message.getText().equals("#exit")) {
                    oInputStream.close();
                    socket.close();
                    break;
                }
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    /**
     * This method is for writing messages from the user and sending them to the server.
     */
    private void serverWriter() {
        try {
            outputStream.writeChars(user.getUsername() + " is joined!");
            String messageText = scanner.nextLine();
            while (!messageText.equals("#exit")) {
                Message message = new Message(user, messageText);
                outputStream.writeObject(message);
                messageText = scanner.nextLine();
            }
            outputStream.writeObject(messageText);
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    /**
     * This method creates two Threads, one for reading from the server and the other 
     * for writing messages.
     */
    public void runThreads() {

        // this thread is for reading the messages from other users and printing them.
        Thread read = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverReader();
                } catch (ClassNotFoundException e) {
                    System.out.println(e);
                    e.printStackTrace();
                }     
            }
        });

        // this thread is for writing messages to sevrer
        Thread write = new Thread(new Runnable() {
            @Override
            public void run() {
                serverWriter();
            } 
        });

        read.start();
        write.start();
    }

    /**
     * This method prints all the previous messages of a channel when a user comes in.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadChannel() throws IOException, ClassNotFoundException {
        outputStream.writeObject(Request.LOAD_CHANNEL);
        ArrayList<Message> messages = (ArrayList<Message>) oInputStream.readObject();
        for (Message message : messages) {
            System.out.println(message);
        }
    }
}
