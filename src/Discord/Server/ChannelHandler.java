package Discord.Server;
import CommonClasses.*;

import java.io.*;
import java.net.*;

public class ChannelHandler implements Runnable{

    private Channel channel;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    // constructor
    public ChannelHandler(Channel channel, Socket socket) {
        this.channel = channel;
        this.socket = socket;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }
}
