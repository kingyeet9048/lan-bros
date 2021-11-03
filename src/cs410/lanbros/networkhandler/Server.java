package cs410.lanbros.networkhandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @CreatedBy Sulaiman Bada, Sheikh Fahad
 * @Version Beta.0.1.0
 */
public class Server implements Runnable {

	// instance variables
	private ServerSocket server;

	private Map<Socket, ServerWorker> workers;

	private String ipAddress;

	private Queue<Request> requestQueue;

	private Router router;

	private PrintWriter writer;

	private Listener listener;

	private String hostName;

	private int MAX_PLAYERS;

	/**
	 * 
	 * @param port        Port number for server to connect to
	 * @param MAX_PLAYERS Max number of players that can play at a single time
	 */
	public Server(int port, int MAX_PLAYERS) {
		try {
			server = new ServerSocket(port);
			workers = new HashMap<>();
			server.getInetAddress();
			String[] hostInfo = InetAddress.getLocalHost().toString().split("/");
			ipAddress = hostInfo[1];
			hostName = hostInfo[0];
			requestQueue = new LinkedList<>();
			this.MAX_PLAYERS = MAX_PLAYERS;
			router = new Router(this);
			System.out.println(hostName);
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

	public String getIpAddress() {
		return ipAddress;
	}

	public int getMAX_PLAYERS() {
		return MAX_PLAYERS;
	}

	public void setMAX_PLAYERS(int mAX_PLAYERS) {
		MAX_PLAYERS = mAX_PLAYERS;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hostName == null) ? 0 : hostName.hashCode());
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + ((listener == null) ? 0 : listener.hashCode());
		result = prime * result + ((requestQueue == null) ? 0 : requestQueue.hashCode());
		result = prime * result + ((router == null) ? 0 : router.hashCode());
		result = prime * result + ((server == null) ? 0 : server.hashCode());
		result = prime * result + ((workers == null) ? 0 : workers.hashCode());
		result = prime * result + ((writer == null) ? 0 : writer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Server other = (Server) obj;
		if (hostName == null) {
			if (other.hostName != null)
				return false;
		} else if (!hostName.equals(other.hostName))
			return false;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (listener == null) {
			if (other.listener != null)
				return false;
		} else if (!listener.equals(other.listener))
			return false;
		if (requestQueue == null) {
			if (other.requestQueue != null)
				return false;
		} else if (!requestQueue.equals(other.requestQueue))
			return false;
		if (router == null) {
			if (other.router != null)
				return false;
		} else if (!router.equals(other.router))
			return false;
		if (server == null) {
			if (other.server != null)
				return false;
		} else if (!server.equals(other.server))
			return false;
		if (workers == null) {
			if (other.workers != null)
				return false;
		} else if (!workers.equals(other.workers))
			return false;
		if (writer == null) {
			if (other.writer != null)
				return false;
		} else if (!writer.equals(other.writer))
			return false;
		return true;
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

					boolean result = router.routeRequest(request);

					System.out.printf("Route %s Processed: %s\n", request.getApi(), result);

					// TODO: Either handle sending here or handle sending in the router
					// TODO: either way, we need to send to that specific person or send to all
					// sockets
					// TODO: depending on what the request is

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
		try {
			// test
			Socket socket = new Socket(server.getIpAddress(), 4321);
			System.out.println(socket.isConnected());
			TimeUnit.SECONDS.sleep(1);
			PrintWriter writer = new PrintWriter(socket.getOutputStream());
			writer.write("/api/something\n");
			writer.flush();
			System.out.println(server.getWorkers().toString());
			writer.write("/terminate\n");
			writer.flush();
			socket.close();
			System.out.println(server.getWorkers().toString());
			server.getServer().close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
