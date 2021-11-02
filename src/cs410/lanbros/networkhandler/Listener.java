package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Listener implements Runnable {

    private ServerSocket listen;
    private Server server;
    private int numberOfConnections = 0;

    public Listener(ServerSocket listen, Server server) {
        this.listen = listen;
        this.server = server;
    }

    public synchronized int getNumberOfConnections() {
        return numberOfConnections;
    }

    public synchronized void setNumberOfConnections(int numberOfConnections) {
        this.numberOfConnections = numberOfConnections;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (!listen.isClosed()) {
            try {
                Socket newConnection = listen.accept();
                if (numberOfConnections == server.getWorkers().size()) {
                    OutputStream stream = newConnection.getOutputStream();
                    if (newConnection.isConnected() && newConnection.isOutputShutdown()) {
                        stream.write("Full Lobby\n".getBytes());
                        stream.flush();
                    }
                    // newConnection.close();
                } else {
                    Map<Socket, ServerWorker> connections = server.getWorkers();
                    ServerWorker newWorker = new ServerWorker(newConnection, server);
                    Thread newServerThread = new Thread(newWorker);
                    newServerThread.start();
                    connections.put(newConnection, newWorker);
                    server.setWorkers(connections);
                    numberOfConnections++;
                }
            } catch (IOException e) {
                System.err.printf("Listener Error: %s\n", e.getMessage());
            }
        }
    }

}
