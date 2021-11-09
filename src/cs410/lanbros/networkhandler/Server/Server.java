package cs410.lanbros.networkhandler.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import cs410.lanbros.networkhandler.Factory;
import cs410.lanbros.networkhandler.Movements;
import cs410.lanbros.networkhandler.Client.Client;

/**
 * Server class to make sure all clients are up to date on the current game.
 * 
 * @CreatedBy Sulaiman Bada
 * @AmendedBy Sheikh Fahad
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

	private boolean gameClosed = false;

	/**
	 * Constructor for server.
	 * 
	 * @param port        Port number for server to connect to
	 * @param MAX_PLAYERS Max number of players that can play at a single time
	 */
	public Server(int port, int MAX_PLAYERS) {
		try {
			server = new ServerSocket(port);
			workers = new ConcurrentHashMap<>();
			String[] hostInfo = server.getInetAddress().getLocalHost().toString().split("/");
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

	/**
	 * 
	 * @return server instance
	 */
	public ServerSocket getServer() {
		return server;
	}

	/**
	 * sets server instance
	 * 
	 * @param server
	 */
	public void setServer(ServerSocket server) {
		this.server = server;
	}

	/**
	 * 
	 * @return socket server map
	 */
	public Map<Socket, ServerWorker> getWorkers() {
		return workers;
	}

	/**
	 * sets socket server map
	 * 
	 * @param workers
	 */
	public void setWorkers(Map<Socket, ServerWorker> workers) {
		this.workers = workers;
	}

	/**
	 * adds a request to the queue
	 * 
	 * @param request
	 */
	public void addToQueue(Request request) {
		requestQueue.add(request);
	}

	/**
	 * gets listener
	 * 
	 * @return
	 */
	public Listener getListener() {
		return listener;
	}

	/**
	 * sets listener
	 * 
	 * @return
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * get max player number
	 * 
	 * @return max number of player
	 */
	public int getMAX_PLAYERS() {
		return MAX_PLAYERS;
	}

	/**
	 * Set max player number
	 * 
	 * @param MAX_PLAYERS
	 */
	public void setMAX_PLAYERS(int MAX_PLAYERS) {
		this.MAX_PLAYERS = MAX_PLAYERS;
	}

	/**
	 * get host name
	 * 
	 * @return host name
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * set host name
	 * 
	 * @param hostName
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Checks to see if the game is closed
	 * 
	 * @return
	 */
	public boolean isGameClosed() {
		return gameClosed;
	}

	/**
	 * Set game close state
	 * 
	 * @param gameClosed
	 */
	public void setGameClosed(boolean gameClosed) {
		this.gameClosed = gameClosed;
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
				if(request == null)
				{
					continue;
				}
				
				try {
					boolean result = router.routeRequest(request);

					System.out.printf("Route %s Processed: %s\n", request.getApi(), result);

				} catch (SocketException e) {
					// something went wrong... adding the request back to the queue and trying again
					System.err.printf("Server Error: %s\n", e.getMessage());
					System.err.println("Client disconnected: " + request.getReceiver().getInetAddress().getHostName());
					System.out.println(getWorkers().toString());
				} catch (IOException e) {
					System.err.printf("Server Error: %s\n", e.getMessage());
					requestQueue.add(request);
					continue;
				}
			}
		}
		for (Map.Entry<Socket, ServerWorker> entry : getWorkers().entrySet()) {
			try {
				entry.getKey().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			entry.getValue().setTerminateThread(true);
			System.out.println("Asked client to terminate...");

		}
	}

	public static void main(String arg[]) {
		Server server = new Server(4321, 4);
		Thread serveThread = new Thread(server);
		serveThread.start();
		Factory factory = new Factory();
		// test
		Client client = new Client(server.getIpAddress(), 4321, true, factory);
		Thread clientThread = new Thread(client);
		clientThread.start();

		try {
			TimeUnit.SECONDS.sleep(1);
			// server.getServer().close();
			PrintWriter writer = new PrintWriter(client.getSocket().getOutputStream());
			writer.write(" " + "/api/movement/" + Movements.MOVE_LEFT.toString() + "\n");
			writer.flush();
			// client.getSocket().close();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
