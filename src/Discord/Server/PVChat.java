package Discord.Server;

import CommonClasses.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class PVChat<List> implements Serializable, Runnable {

    private User user1;
    private User user2;
    private Map<Integer, PVHandler> pvHandlers;
    private ArrayList<Message> messages;
    private ArrayList<String> filePaths;
    private boolean isUser1Blocked = false;
    private boolean isUser2Blocked = false;
    private int portNumber;
    private Random random = new Random();
    private static Socket currentSocket;

    
    // constructor
    public PVChat(User user1, User user2) {
        this.user1 = user1;
        this.user2 = user2;
        messages = new ArrayList<>();
        filePaths = new ArrayList<>();
        pvHandlers = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            portNumber = serverSocket.getLocalPort();
            System.out.println("Now the PVCHat is listening and waiting for clients...");
            Socket socket;
            int handler_ID;
            while (true) {
                socket = serverSocket.accept();
                System.out.println("A user is entered!");
                handler_ID = random.nextInt();
                PVHandler pvHandler = new PVHandler(this, socket, handler_ID);
                pvHandlers.put(handler_ID, pvHandler);
                new Thread(pvHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fileServerSocketListen() {
        try {
            ServerSocket serverSocket = new ServerSocket(7000);
            Socket socket;
            while (true) {
                socket = serverSocket.accept();
                currentSocket = socket;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static Socket getCurrentSocket() {
        return currentSocket;
    }

    public boolean isBlocked(String username) {
        if (user1.getUsername().equalsIgnoreCase(username) && isUser1Blocked) {
            return true;
        } else if (user2.getUsername().equalsIgnoreCase(username) && isUser2Blocked) {
            return true;
        } else {
            return false;
        }
    }

    public User getUser1() {
        return user1;
    }

    public User getUser2() {
        return user2;
    }

    public int getPortNumber() {
        return portNumber;
    }


    public synchronized void addMessage(Message message) {
        messages.add(message);
        System.out.println("The message is added!");
    }

    public synchronized void addFile(String filePath) {
        filePaths.add(filePath);
    }


    public ArrayList<Message> getMessages() {
        return messages;
    }

    public synchronized void sendMessage(Message message, int handler_ID) throws IOException {
        for (Map.Entry<Integer, PVHandler> entry : pvHandlers.entrySet()) {
            if ((!entry.getKey().equals(handler_ID)) && entry.getValue().getIsOnline()) {
                entry.getValue().getObjectOutputStream().writeObject(message);
            }
        }
    }

    public synchronized void removeHandlerMap(int handler_ID) {
        pvHandlers.remove(handler_ID);
    }


    public void block(String username) {
        if (user1.getUsername().equalsIgnoreCase(username)) {
            isUser2Blocked = true;
        } else {
            isUser1Blocked = true;
        }
    }

    public ArrayList<String> getFilePaths() {
        return filePaths;
    }

    public  FileMessage doesFileExist(String filename) {
        for (Message message : messages) {
            if (message.getText().equalsIgnoreCase(filename)) {
                return (FileMessage) message;
            }
        }
        return null;
    }

}
