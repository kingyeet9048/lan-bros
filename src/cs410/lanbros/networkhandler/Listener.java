package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

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
                System.out.println(
                        "Received new Connection from: " + newConnection.getInetAddress().getHostName());
                if (server.getWorkers().size() >= server.getMAX_PLAYERS()) {
                    OutputStream stream = newConnection.getOutputStream();
                    if (newConnection.isConnected() && newConnection.isOutputShutdown()) {
                        stream.write("Full Lobby\n".getBytes());
                        stream.flush();
                    }
                    // newConnection.close();
                } else {
                    ServerWorker newWorker = new ServerWorker(newConnection, server);
                    Thread newServerThread = new Thread(newWorker);
                    newServerThread.start();
                    server.getWorkers().put(newConnection, newWorker);
                    System.out.println("Connection Added to list");
                }
            } catch (IOException e) {
                System.err.printf("Listener Error: %s\n", e.getMessage());
                break;
            }
        }
    }

}
