package Discord.Server;

import Discord.Server.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class TextChannel extends TextChat implements Runnable{

    private final String channelName;
    private transient ServerSocket serverSocket;
    private int portNumber;
    private final Random random = new Random();


    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(0);
            portNumber = serverSocket.getLocalPort();
            System.out.println("Now the PVCHat is listening and waiting for clients...");
            Socket socket;
            int handler_ID;
            while (true) {
                socket = serverSocket.accept();
                System.out.println("A user is entered!");
                handler_ID = random.nextInt();
                ChannelHandler channelHandler = new ChannelHandler(socket, handler_ID,this);
                chatHandlers.put(handler_ID, channelHandler);
                new Thread(channelHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public TextChannel(String channelName) {
        this.channelName = channelName;
    }


    public String getChannelName() {
        return channelName;
    }

    public int getPortNumber() {
        return portNumber;
    }
}