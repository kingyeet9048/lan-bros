package cs410.lanbros.networkhandler.Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Client class that handles interactions with the server and game manager
 * controller. Current Capabilities: Connect to server with a timeout Disconnect
 * from server and shutdown all threads Keep a list of current players Router to
 * route the response to the respective logic (uses a concurrent queue which
 * allow other thread to update it at the same time)
 * 
 * @author Sulaiman Bada
 */
public class Client implements Runnable {

	// instance variables
	private Socket socket;
	private List<String> currentPlayer;
	private String serverAddress;
	private int serverPort;
	private final int ATTEMPT_BEFORE_TIMEOUT = 10;
	private Queue<Response> reponseQueue;
	private ResponseRouter router;
	private boolean isHost;

	/**
	 * Constuctor needs to know the address to connect to, the port to connect to,
	 * and whether this client is the host.
	 * 
	 * @param serverAddress
	 * @param serverPort
	 * @param isHost
	 */
	public Client(String serverAddress, int serverPort, boolean isHost) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.isHost = isHost;
		reponseQueue = new ConcurrentLinkedQueue<>();
		router = new ResponseRouter(this);
		currentPlayer = new LinkedList<>();
	}

	/**
	 * Joins a game given a server port, server address, and a max timout amount.
	 */
	public boolean joinGame() {
		boolean hasJoined = false;
		int attemptNumber = 0;
		while (true) {
			try {
				// trys to connect to the addess and port.
				socket = new Socket(serverAddress, serverPort);
				System.out.println("Connected to the game!");
				// we are past the socket line which means we joined.
				hasJoined = true;
				// send an api call to let server know that client has joined
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				writer.write(" /api/conn/client/connection\n");
				writer.flush();
				addPlayerToList(socket.getInetAddress().getHostName());
				// writer.close();
				break;
			} catch (IOException e) {
				try {
					// something happened and we were not able to join the game. Increase the couter
					// and check to see if we have reached timeout
					attemptNumber++;
					if (attemptNumber >= ATTEMPT_BEFORE_TIMEOUT) {
						System.err.printf("Reached max number of attempts (%d). Stopping and closing...",
								ATTEMPT_BEFORE_TIMEOUT);
						break;
					}
					System.err.println("Could not connect to game. Trying agin in 10 second...");
					Thread.sleep(10000);
					continue;
				} catch (InterruptedException e1) {
					System.err.printf("Client Joining Game Error: %s\n", e1.getMessage());
					break;
				}
			}
		}
		return hasJoined;
	}

	/**
	 * Adds a new response to the end of the queue
	 * 
	 * @param response
	 */
	public void appendToQueue(Response response) {
		reponseQueue.add(response);
	}

	/**
	 * Adds a new player to end of the list
	 * 
	 * @param player
	 */
	public void addPlayerToList(String player) {
		// the player cannot already be added to the list.
		if (!currentPlayer.contains(player)) {
			currentPlayer.add(player);
			System.out.println("Player list updated: " + currentPlayer.toString());
		}
	}

	/**
	 * Removes a player from the list.
	 * 
	 * @param player
	 */
	public void removePlayerFromList(String player) {
		// player cannot already exist inside the list.
		if (currentPlayer.contains(player)) {
			currentPlayer.remove(player);
			System.out.println("Player list updated: " + currentPlayer.toString());
		}
	}

	/**
	 * update the players rendered. This is in the case of a new conneciton that
	 * hasnt been rendered on the GUI. The logic is still in progress.
	 */
	public void updatePlayers() {
		System.out.println("Would update players here");
	}

	/**
	 * Is the current client a host?
	 * 
	 * @return
	 */
	public boolean isHost() {
		return isHost;
	}

	/**
	 * Set the host
	 * 
	 * @param isHost
	 */
	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	/**
	 * Starts the game by allowing the player to move and starting the respective
	 * gameGUI tools
	 */
	public void startGame() {
		// TODO
	}

	/**
	 * Ends the game. Moves the player to the end screen and disables their moving
	 * ability.
	 */
	public void endGame() {
		// TODO
	}

	/**
	 * moves a player given the direction and the player to move.
	 * 
	 * @param movement
	 * @param player
	 */
	public void movePlayer(String movement, String player) {
		// TODO: Move player with given ENUM
		System.out.println("This is where a player would be moved: " + player + " " + movement);
	}

	/**
	 * get the socket connected to the server.
	 * 
	 * @return
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * Sets the socket
	 * 
	 * @param socket
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {

		boolean joined = joinGame();

		if (joined) {
			// reponse listener here (listens for a reponse)
			ResponseListener responseListener = new ResponseListener(socket, this);
			Thread responseListenerThread = new Thread(responseListener);
			responseListenerThread.start();
			while (!socket.isClosed()) {
				// if there is something in the queue
				if (!reponseQueue.isEmpty()) {
					// poll it and process it through the router
					Response response = reponseQueue.poll();

					boolean result = router.handleResponse(response);

					System.out.printf("Response %s Processed: %s\n", response.getRawReponse(), result);
				}
			}
			System.out.println("\nLost connection with the server goodbye\n");
		} else {
			System.err.println("Currently going to kill the client if it cannot join the server...");
		}

	}

}
