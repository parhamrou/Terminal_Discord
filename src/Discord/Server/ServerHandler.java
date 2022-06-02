package Discord.Server;

import java.net.Socket;

public class ServerHandler implements Runnable{

    private Server server;
    private Socket socket;

    public ServerHandler(Server server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }


    @Override
    public void run() {
        
    }
}
