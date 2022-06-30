package Discord.Server;

import CommonClasses.*;

import java.io.*;
import java.net.*;
import java.util.*;

public abstract class TextChat implements Serializable{

    protected Map<Integer, ChatHandler> chatHandlers; // must update
    protected ArrayList<Message> messages;
    protected ArrayList<String> filePaths;
    private static Socket currentSocket;


    public TextChat() {
        messages = new ArrayList<>();
        filePaths = new ArrayList<>();
        chatHandlers = new HashMap<>();
    }


    public static void fileServerSocketListen() {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
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
        for (Map.Entry<Integer, ChatHandler> entry : chatHandlers.entrySet()) {
            if ((!entry.getKey().equals(handler_ID)) && entry.getValue().getIsOnline()) {
                entry.getValue().getObjectOutputStream().writeObject(message);
            }
        }
    }

    public synchronized void removeHandlerMap(int handler_ID) {
        chatHandlers.remove(handler_ID);
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
