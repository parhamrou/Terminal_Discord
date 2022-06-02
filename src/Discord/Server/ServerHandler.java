package Discord.Server;

import java.io.*;
import java.net.Socket;

public class ServerHandler implements Runnable{

    private Server server;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public ServerHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
        try {
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        
    }
}
