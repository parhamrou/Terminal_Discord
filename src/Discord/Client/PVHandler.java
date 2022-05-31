package  Discord.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import CommonClasses.*;
/**
 * This class is for handling one PV chat and showing its options when a user is working with that.
 */
public class PVHandler {
    
    private PVChat pvChat;
    private Socket socket;
    private ObjectInputStream oInputStream;
    private ObjectOutputStream outputStream;
    private Scanner scanner = new Scanner(System.in);


    // constructor
    public PVHandler(PVChat pvChat, int portNumber) {
        this.pvChat = pvChat;
        try {
            socket = new Socket("localhost", portNumber);
            oInputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
        }
    }


    public void start() {
        while (true) {
            MenuPrinter.PVMenu();
            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        loadPV();
                        runThreads();
                        break;
                    case 2:
                        block();
                        break;
                    case 3:
                        return;
                    default:
                        System.out.println("Your input is invalid!");
                        continue;
                }
            } catch (InputMismatchException e) {
                System.out.println("Your input is not valid!");
                continue;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This method is for sending the 'block' request to the server. 
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void block() throws IOException, ClassNotFoundException {
        outputStream.writeObject(Request.BLOCK_IN_PV);
        outputStream.writeObject(pvChat);
        boolean isSuccessful = (boolean) oInputStream.readObject();
        if (isSuccessful) {
            System.out.println("Now this user is blocked!");
        } else {
            System.out.println("You blocked this user before!");
        }
    }

    
    private void serverReader() throws ClassNotFoundException {
        try {
            Message message = null;
            while (true) {
                message = (Message) oInputStream.readObject();
                if (message.getText().equals("#exit")) {
                    outputStream.close();
                    socket.close();
                    break;
                }
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }


    private void serverWriter() {
        try {
            String messageText = scanner.nextLine();
            while (!messageText.equals("#exit")) {
                Message message = new Message(pvChat.getUser1(), messageText);
                outputStream.writeObject(message);
                messageText = scanner.nextLine();
            }
            outputStream.writeObject(messageText);
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void runThreads() {

        // this thread is for reading the messages from other users and printing them.
        Thread read = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    serverReader();
                } catch (ClassNotFoundException e) {
                    System.out.println(e);
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


    public void loadPV() throws IOException, ClassNotFoundException {
        outputStream.writeObject(Request.LOAD_PV);
        ArrayList<Message> messages = (ArrayList<Message>) oInputStream.readObject();
        for (Message message : messages) {
            System.out.println(message);
        }
    }
}
