package Discord.Server;

import CommonClasses.*;

import java.io.*;
import java.net.*;

public class ChannelHandler extends ChatHandler implements Runnable, Serializable{


    public ChannelHandler(Socket socket, int handler_ID, TextChannel textChat) {
        super(socket, handler_ID, textChat);
    }

    @Override
    public void run() {
        try {
            this.username = (String) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Request clientRequest;
        while (true) {
            try {
                System.out.println("Waiting for request in Channelhandler in server side...");
                clientRequest = (Request) objectInputStream.readObject();
                System.out.println("Request: " + clientRequest.toString());
                switch (clientRequest) {
                    case LOAD_CHAT:
                        objectOutputStream.writeUnshared(chat.getMessages());
                        break;
                    case ENTER_CHAT:
                        isOnline = true;
                        Receiver();
                        isOnline = false;
                        break;
                    case EXIT:
                        objectOutputStream.close();
                        objectInputStream.close();
                        chat.removeHandlerMap(handler_ID);
                        return;
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
