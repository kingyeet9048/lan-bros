package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Listener implements Runnable {

    private ServerSocket listen;
    private Server server;

    public Listener(ServerSocket listen, Server server) {
        this.listen = listen;
        this.server = server;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (!listen.isClosed()) {
            try {
                Socket newConnection = listen.accept();
                List<Socket> connections = server.getConnectionSockets();
                connections.add(newConnection);
                server.setConnectionSockets(connections);
            } catch (IOException e) {
                System.err.printf("Listener Error: %s\n", e.getMessage());
            }
        }
    }

}
