package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Server implements Runnable {

	// instance variables
	private ServerSocket server;

	private Map<Socket, ServerWorker> workers;

	private String ipAddress;

	private Queue<Request> requestQueue;

	private Router router;

	private PrintWriter writer;

	private Listener listener;

	public Server(int port, int MAX_PLAYERS) {
		try {
			server = new ServerSocket(port);
			workers = new HashMap<>();
			server.getInetAddress();
			ipAddress = InetAddress.getLocalHost().toString().split("/")[1];
			requestQueue = new LinkedList<>();
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

	public void addToQueue(Request request) {
		requestQueue.add(request);
	}

	public Listener getListener() {
		return listener;
	}

	@Override
	public void run() {
		// start a listener to accept connections
		listener = new Listener(server, this);
		Thread listenerThread = new Thread(listener);
		listenerThread.start();
		while (!server.isClosed()) {
			// check to see if there is an request in the queue
			if (requestQueue.size() >= 1) {
				// poll the request from the queue
				Request request = requestQueue.poll();

				// will output the payload and all we do is send the result of it.

				try {
					// make a new printwriter everytime because the connection socket could change
					writer = new PrintWriter(request.getReceiver().getOutputStream());
					// doesnt have to be string
					// check to see if output is open and the socket is connected
					if (!request.getReceiver().isOutputShutdown() && request.getReceiver().isConnected()) {
						// might have to add \n... dont know yet
						writer.write((String) router.routeRequest(request.getApi()));
					}
				} catch (IOException e) {
					// something went wrong... adding the request back to the queue and trying again
					System.err.printf("Server Error: %s\n", e.getMessage());
					requestQueue.add(request);
					continue;
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
