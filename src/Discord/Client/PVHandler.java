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
public class PVHandler {

    private User user;
    private Socket socket;
    private ObjectInputStream oInputStream;
    private ObjectOutputStream outputStream;
    private int portNumber;
    private Scanner scanner = new Scanner(System.in);
    private final Object mutexObject = new Object();

    // constructor
    public PVHandler(int portNumber, User user) {
        try {
            this.user = user;
            this.portNumber = portNumber;
            socket = new Socket("localhost", portNumber); // now, this class connects with socket to the PVHandler class in server side
            oInputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                        outputStream.writeObject(Request.LOAD_PV);
                        boolean isSuccessful = (boolean) oInputStream.readObject();
                        if (!isSuccessful) {
                            System.out.println("Your friend has blocked you, so you can't send any messages to him/her!");
                            break;
                        }
                        loadPV();
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


    private void serverReader() throws ClassNotFoundException {
        try {
            Request request;
            Message message = null;
            while (true) {
                message = (Message) oInputStream.readObject();
                if (message.getText().equalsIgnoreCase("#exit")) {
                    break;
                }
                System.out.println(message);
            }
            } catch(IOException e){
                System.out.println(e);
                e.printStackTrace();
            }
        }


        private void serverWriter () throws ClassNotFoundException {
            try {
                Message message = null;
                // try with creating a new scanner :/
                String messageText = scanner.nextLine();
                while (!messageText.equalsIgnoreCase("#exit")) {
                    if (messageText.equalsIgnoreCase("#send file")) {
                        outputStream.writeObject(Request.SEND_FILE);
                        Socket socket = new Socket("localhost", 7000); // connecting to the file serverSocket
                        System.out.println("Enter the path of the file:\n> ");
                        String filePath = scanner.nextLine();
                        new Thread(new Runnable() { // this thread gets sends file in different thread
                            @Override
                            public void run() {
                                sendFile(filePath, socket);
                            }
                        }).start();
                    } else if (messageText.equalsIgnoreCase("#download file")) {
                        outputStream.writeObject(Request.DOWNLOAD_FILE);
                        Socket socket = new Socket("localhost", 7000);
                        downloadFile(socket);
                    } else {
                        outputStream.writeObject(Request.TEXT_MESSAGE);
                        outputStream.writeObject(new TextMessage(user, messageText));
                    }
                    messageText = scanner.nextLine();
                }
                outputStream.writeObject(Request.TEXT_MESSAGE);
                outputStream.writeObject(new TextMessage(user, messageText));
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            }
        }

        public void runThreads () throws IOException, ClassNotFoundException {
            outputStream.writeObject(Request.ENTER_PV);
            System.out.println("Exit: '#exit', Send file: '#send file', Download file: #download file");
            // this thread is for reading the messages from other users and printing them.
            Thread read = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serverReader();
                    } catch (ClassNotFoundException e) {
                        System.out.println(e);
                    }
                }
            });

            // this thread is for writing messages to server
            Thread write = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serverWriter();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });

            read.start();
            write.start();
            try {
                read.join();
                write.join();
                return;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        private void sendFile (String filePath, Socket socket){
            try {
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                File file = new File(filePath);
                byte[] content = Files.readAllBytes(file.toPath());
                objectOutputStream.writeObject(new FileMessage(user, file.getName(), content));
                objectOutputStream.close();
                socket.close();
                System.out.println("The file is sent!");
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        private void loadPV () throws IOException, ClassNotFoundException {
            ArrayList<Message> messages = (ArrayList<Message>) oInputStream.readObject();
            if (messages.size() == 0) {
                System.out.println("There is no message in this chat before!");
                return;
            }
            for (Message message : messages) {
                System.out.println(message);
            }
        }

        private void downloadFile (Socket socket) throws IOException, ClassNotFoundException {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ArrayList<String> files = (ArrayList<String>) objectInputStream.readObject();
            if (files.size() == 0) {
                System.out.println("There is no file in this chat!");
                objectOutputStream.writeObject(Request.BACK);
                return;
            }
            System.out.print("Enter the path where you want to save to file, like: D:\\\\:\n> ");
            String path = scanner.nextLine();
            for (String fileName : files) {
                System.out.println("- " + fileName);
            }
            objectOutputStream.writeObject(Request.CHECK_FILE);
            System.out.print("Enter the name of the file you want to download\n> ");
            String fileName = scanner.nextLine();
            objectOutputStream.writeObject(fileName);
            boolean doesFileExist = (boolean) objectInputStream.readObject();
            if (!doesFileExist) {
                System.out.println("There is no file with this name!");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        FileMessage fileMessage = (FileMessage) objectInputStream.readObject();
                        File file = new File(path + fileMessage.getText());
                        Files.write(file.toPath(), fileMessage.getContent());
                        objectInputStream.close();
                        objectOutputStream.close();
                        socket.close();
                        System.out.println("Download is finished");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println("Downloading...");
        }
    }
