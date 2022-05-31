package Discord.Server;

import java.io.*;
import java.net.*;

public class MainServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(2000);
            Socket socket;
            ServerConnectionHandler serverConnectionHandler;
            while (true) {
                socket = serverSocket.accept();
                Thread thread = new Thread(new ServerConnectionHandler(socket));
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Connection Error!");
        }
    }
}
