package  Discord.Client;

import java.io.IOException;
import java.net.Socket;


public class Main {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 2000);
            ClientHandler clientHandler = new ClientHandler(socket);
            clientHandler.start();
        } catch (IOException e) {
            System.out.println("There is a problem in your connection!");
            e.printStackTrace();
        }
    }
}
