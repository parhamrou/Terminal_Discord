package  CommonClasses;

import Discord.Server.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public abstract class Channel implements Runnable, Serializable {
    
    private String name;
    private List<User> users;
    private List<Message> messages;
    private List<Message> pinnedMessages;
    private int serverPortNumber;

    // constructor
    public Channel(String name) {
        this.name = name;
        users = Collections.synchronizedList(new ArrayList<>());
        messages = Collections.synchronizedList(new ArrayList<>());
        pinnedMessages = Collections.synchronizedList(new ArrayList<>());
    }


    @Override
    public void run() {
        Socket socket;
        System.out.println("Now the channel '" + name + "' is listening...");
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            serverPortNumber = serverSocket.getLocalPort();
            ExecutorService executorService = Executors.newCachedThreadPool();
            // waiting for a client to connect and pass it to a new thread.
            while (true)  {
                try {
                    socket = serverSocket.accept();
                    System.out.println("Now a client is connected to the channel '" + name + "' with port number: " + socket.getPort());
                    executorService.execute(new ChannelHandler(this, socket)); // passing the thread to the thread pool to execute
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public List<Message> getMessages() {
        return messages;
    }


    public List<Message> getPinnedMessages() {
        return pinnedMessages;
    }


    public String getName() {
        return name;
    }

    public int getServerPortNumber() {
        return serverPortNumber;
    }
}
