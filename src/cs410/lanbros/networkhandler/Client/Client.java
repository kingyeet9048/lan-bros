package cs410.lanbros.networkhandler.Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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

	public Client(String serverAddress, int serverPort, boolean isHost) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.isHost = isHost;
		reponseQueue = new ConcurrentLinkedQueue<>();
		router = new ResponseRouter(this);
		currentPlayer = new LinkedList<>();
	}

	public boolean joinGame() {
		boolean hasJoined = false;
		int attemptNumber = 0;
		while (true) {
			try {
				socket = new Socket(serverAddress, serverPort);
				System.out.println("Connected to the game!");
				hasJoined = true;
				// send an api call to let server know that client has joined
				PrintWriter writer = new PrintWriter(socket.getOutputStream());
				writer.write(" /api/conn/client/connection\n");
				writer.flush();
				// writer.close();
				break;
			} catch (IOException e) {
				try {
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

	public void appendToQueue(Response response) {
		reponseQueue.add(response);
	}

	public void addPlayerToList(String player) {
		if (!currentPlayer.contains(player)) {
			currentPlayer.add(player);
			System.out.println("Player list updated: " + currentPlayer.toString());
		}
	}

	public void removePlayerFromList(String player) {
		if (currentPlayer.contains(player)) {
			currentPlayer.remove(player);
			System.out.println("Player list updated: " + currentPlayer.toString());
		}
	}

	public void updatePlayers() {
		System.out.println("Would update players here");
	}

	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}

	public void startGame() {
		// TODO
	}

	public void endGame() {
		// TODO
	}

	public void movePlayer(String movement, String player) {
		// TODO: Move player with given ENUM
		System.out.println("This is where a player would be moved: " + player + " " + movement);
	}

	public Socket getSocket() {
		return socket;
	}

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
