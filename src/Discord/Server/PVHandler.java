package Discord.Server;
import CommonClasses.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class PVHandler implements Runnable{

    private final PVChat pvChat;
    private String username;
    private final int handler_ID;
    private final Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private boolean isOnline;

    public PVHandler(PVChat pvChat, Socket socket, int handler_ID) {
        this.pvChat = pvChat;
        this.socket = socket;
        this.handler_ID = handler_ID;
        this.isOnline = false;
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            this.username = (String) objectInputStream.readObject();
            System.out.println("The username is received: " + username);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Request clientRequest;
        while (true) {
            try {
                System.out.println("Waiting for request in PVhandler in server side...");
                clientRequest = (Request) objectInputStream.readObject();
                System.out.println("Request: " + clientRequest.toString());
                switch (clientRequest) {
                    case BLOCK_IN_PV:
                        pvChat.block((String) objectInputStream.readObject());
                        break;
                    case LOAD_PV:
                        if (pvChat.isBlocked(username)) {
                            objectOutputStream.writeObject(false);
                            break;
                        } else {
                            objectOutputStream.writeObject(true);
                        }
                        objectOutputStream.writeUnshared(pvChat.getMessages());
                        break;
                    case ENTER_PV:
                        isOnline = true;
                        Receiver();
                        isOnline = false;
                        break;
                    case EXIT:
                        objectOutputStream.close();
                        objectInputStream.close();
                        pvChat.removeHandlerMap(handler_ID);
                        return;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }


    private void Receiver() throws IOException, ClassNotFoundException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        TextMessage message;
        Request request;
        while (true) {
            request = (Request) objectInputStream.readObject();
            System.out.println("Request: " + request.toString());
            if (request == Request.TEXT_MESSAGE) {
                message = (TextMessage) objectInputStream.readObject();
                if (message.getText().equalsIgnoreCase("#exit")) {
                    objectOutputStream.writeObject(message);
                    return;
                }
                pvChat.addMessage(message);
                pvChat.sendMessage(message, handler_ID);
            } else if (request == Request.SEND_FILE){
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveFile();
                    }
                });
            } else {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            sendFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private void saveFile() {
        try {
            Socket currentSocket = PVChat.getCurrentSocket();
            ObjectInputStream objectInputStream = new ObjectInputStream(currentSocket.getInputStream());
            FileMessage fileMessage = (FileMessage) objectInputStream.readObject();
            File file = new File("D:\\data\\" + fileMessage.getText());
            Files.write(file.toPath(), fileMessage.getContent());
            pvChat.addFile(fileMessage.getText());
            pvChat.addMessage(fileMessage);
            pvChat.sendMessage(fileMessage, handler_ID);
            System.out.println("File is saved!");
            objectInputStream.close();
            currentSocket.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendFile() throws IOException, ClassNotFoundException {
        Socket currentSocket = PVChat.getCurrentSocket();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(currentSocket.getOutputStream());
        ObjectInputStream objectInputStream = new ObjectInputStream(currentSocket.getInputStream());
        objectOutputStream.writeUnshared(pvChat.getFilePaths());
        Request request = (Request) objectInputStream.readObject();
        if (request == Request.CHECK_FILE) {
            String fileName = (String) objectInputStream.readObject();
            FileMessage fileMessage = pvChat.doesFileExist(fileName);
            if (fileMessage == null) {
                objectOutputStream.writeObject(false);
            } else {
                objectOutputStream.writeObject(true);
                objectOutputStream.writeObject(fileMessage);
            }
        }
        objectInputStream.close();
        objectOutputStream.close();
        currentSocket.close();
    }

    public boolean getIsOnline() {
        return isOnline;
    }
}
