package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

	// instance variables
	private ServerSocket server;

	private List<Socket> connectionSockets;

	public Server(int port) {
		// TODO Auto-generated constructor stub
		try {
			server = new ServerSocket(port);
			connectionSockets = new ArrayList<>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.printf("Server Error: %s\n", e.getMessage());
		}
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public List<Socket> getConnectionSockets() {
		return connectionSockets;
	}

	public void setConnectionSockets(List<Socket> connectionSockets) {
		this.connectionSockets = connectionSockets;
	}

	@Override
	public void run() {
		// start a listener to accept connections
		Listener listener = new Listener(server, this);
		Thread listenerThread = new Thread(listener);
		listenerThread.start();

	}

}
