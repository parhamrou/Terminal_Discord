package Discord.Client;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;

import CommonClasses.*;
import Discord.Server.*;

/**
 * This class is for handling one PV chat and showing its options when a user is working with that.
 */
public class PVHandler extends ChatHandler {


    public PVHandler(int portNumber, User user) {
        super(portNumber, user);
    }

    public void start() throws IOException {
        outputStream.writeObject(user.getUsername());
        while (true) {
            MenuPrinter.PVMenu();
            try {
                int choice = scanner.nextInt();
                if (choice > 3 || choice < 0) {
                    System.out.println("Your input is invalid!");
                    continue;
                }
                switch (choice) {
                    case 1:
                        outputStream.writeObject(Request.LOAD_CHAT);
                        boolean isSuccessful = (boolean) oInputStream.readObject();
                        if (!isSuccessful) {
                            System.out.println("Your friend has blocked you, so you can't send any messages to him/her!");
                            break;
                        }
                        loadChat();
                        scanner.nextLine();
                        runThreads();
                        break;
                    case 2:
                        block();
                        break;
                    case 3:
                        outputStream.writeObject(Request.EXIT);
                        return;
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
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void block() throws IOException, ClassNotFoundException {
        outputStream.writeObject(Request.BLOCK_IN_PV);
        outputStream.writeObject(user.getUsername());
        System.out.println("Now this user is blocked!");
    }
}
