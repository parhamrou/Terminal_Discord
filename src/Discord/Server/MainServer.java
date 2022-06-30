package Discord.Server;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class MainServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(2000);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TextChat.fileServerSocketListen();
                }
            }).start();
            ExecutorService executorService = Executors.newCachedThreadPool(); // using thread pool for executing the threads.
            Socket socket;
            ClientHandler clientHandler;
            while (true) {
                socket = serverSocket.accept();
                System.out.println("A client is connected to the main ServerSocket with port number: " + socket.getLocalPort());
                executorService.execute(new ClientHandler(socket));
            }
        } catch (IOException e) {
            System.out.println("Connection Error!");
            e.printStackTrace();
        }
    }
}
