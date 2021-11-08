package cs410.lanbros.networkhandler.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server Listener for new connections
 * 
 * @author Sulaiman Bada
 */
public class Listener implements Runnable {

    // instance variables
    private ServerSocket listen;
    private Server server;

    /**
     * Constuctor needs server socket and server instance.
     * 
     * @param listen
     * @param server
     */
    public Listener(ServerSocket listen, Server server) {
        this.listen = listen;
        this.server = server;
    }

    @Override
    public void run() {
        // while server is not closed
        while (!listen.isClosed()) {
            try {
                // accepts a new connection
                Socket newConnection = listen.accept();
                System.out.println("Received new Connection from: " + newConnection.getInetAddress().getHostName());
                // if the connection size is greater then the server limit
                if (server.getWorkers().size() >= server.getMAX_PLAYERS()) {
                    // tell the connection that the lobby is full
                    OutputStream stream = newConnection.getOutputStream();
                    if (newConnection.isConnected() && newConnection.isOutputShutdown()) {
                        stream.write("Full Lobby\n".getBytes());
                        stream.flush();
                    }
                    // newConnection.close();
                } else {
                    // lobby not full, process the connection by giving it its own worker and start
                    // its thread.
                    // this worker will listen for any requests from it.
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
