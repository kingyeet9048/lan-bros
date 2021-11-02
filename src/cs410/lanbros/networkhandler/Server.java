package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Server implements Runnable {

	// instance variables
	private ServerSocket server;

	private Map<Socket, ServerWorker> workers;

	private String ipAddress;

	public Server(int port, int MAX_PLAYERS) {
		try {
			server = new ServerSocket(port);
			workers = new HashMap<>();
			server.getInetAddress();
			ipAddress = InetAddress.getLocalHost().toString().split("/")[1];
			System.out.println(ipAddress);
		} catch (IOException e) {
			System.err.printf("Server Error: %s\n", e.getMessage());
		}
	}

	public ServerSocket getServer() {
		return server;
	}

	public void setServer(ServerSocket server) {
		this.server = server;
	}

	public Map<Socket, ServerWorker> getWorkers() {
		return workers;
	}

	public void setWorkers(Map<Socket, ServerWorker> workers) {
		this.workers = workers;
	}

	@Override
	public void run() {
		// start a listener to accept connections
		Listener listener = new Listener(server, this);
		Thread listenerThread = new Thread(listener);
		listenerThread.start();
		while (!server.isClosed()) {
			// check to see if a connection needs a worker...
			for (Map.Entry<Socket, ServerWorker> entry : workers.entrySet()) {
				// key
				Socket currentKey = entry.getKey();
				// value
				ServerWorker currentValue = entry.getValue();

				if (currentValue == null) {
					ServerWorker newWorker = new ServerWorker(currentKey);
					workers.put(currentKey, newWorker);
					Thread newServerThread = new Thread(newWorker);
					newServerThread.start();
				}
			}
		}
	}

	public static void main(String arg[]) {
		Server server = new Server(4321, 4);
		Thread serveThread = new Thread(server);
		serveThread.start();
	}

}
