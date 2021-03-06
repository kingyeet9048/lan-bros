package networkhandler.client.java;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import content.npc.java.ServerPlayerNPC;
import gui.state.java.InMultiplayerGameState;
import io.java.KeyBind;
import main.java.Main;
import networkhandler.shared.java.Factory;
import networkhandler.shared.java.NetPacket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JOptionPane;

import content.npc.java.NPC;

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
	private String thisPlayerName;
	private List<String> currentPlayer;
	private String serverAddress;
	private int serverPort;
	private final int ATTEMPT_BEFORE_TIMEOUT = 1;
	private Queue<Response> reponseQueue;
	private boolean isHost;
	private PrintWriter writer;
	private Factory factory;
	private Level currentLevel;
	private ClientPlayerNPC thisPlayer;
	@SuppressWarnings("unused")
	private InMultiplayerGameState gui;
	private boolean canMove = false;

	/**
	 * Constuctor needs to know the address to connect to, the port to connect to,
	 * and whether this client is the host.
	 * 
	 * @param serverAddress
	 * @param serverPort
	 * @param isHost
	 * @param username
	 */
	public Client(String serverAddress, int serverPort, boolean isHost, String username, Factory factory) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.isHost = isHost;
		reponseQueue = new ConcurrentLinkedQueue<>();
		currentPlayer = new LinkedList<>();
		this.factory = factory;
		this.thisPlayerName = username;
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
				// new Thread(() -> {
				// JOptionPane.showMessageDialog(null, "Please wait while we try to connect to
				// the game...");
				// }).start();
				socket = new Socket(serverAddress, serverPort);
				System.out.println("Connected to the game!");
				// we are past the socket line which means we joined.
				hasJoined = true;
				// send an api call to let server know that client has joined
				writer = new PrintWriter(socket.getOutputStream());
				writer.write(" /api/conn/client/connection/" + thisPlayerName + "\n");
				writer.flush();
				addPlayerToList(thisPlayerName);
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
						new Thread(() -> {
							JOptionPane.showMessageDialog(null,
									"Reached max number of attempts. Stopping and closing...: ");

						}).start();
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
	public synchronized void addPlayerToList(String player) {
		// the player cannot already be added to the list.
		if (!currentPlayer.contains(player)) {
			currentPlayer.add(player);

			if (currentLevel != null) {
				currentLevel.playerSet.add(new ServerPlayerNPC(currentLevel, 128, 128, player));
			}

			System.out.println("Player list updated: " + currentPlayer.toString());
		}
	}

	public synchronized void syncPlayerCoordinates() {
		if (isHost) {
			String content = "";
			for (ClientPlayerNPC player : currentLevel.playerSet) {
				content += player.npcX + "," + player.npcY + "," + player.playerName + "_";
			}

			writer.write(" /api/playersync/" + content.substring(0, content.length() - 1) + "\n");
			writer.flush();
		}
	}

	public synchronized void updateClientPlayerPosition() {
		writer.write(" /api/setposmotion/" + thisPlayer.npcX + "_" + thisPlayer.npcY + "_" + thisPlayer.motionX + "_"
				+ thisPlayer.motionY + (thisPlayer.wallHit != null ? "_" + thisPlayer.wallHit.ordinal() : "") + "\n");
		writer.flush();
	}

	public synchronized void applyPlayerSync(String api) {
		if (currentLevel != null) {
			String[] players = api.split("_");

			for (String player : players) {
				if (player != null && player.length() > 0) {
					String[] comps = player.substring(player.lastIndexOf("/") + 1).split(",");
					for (ClientPlayerNPC play : currentLevel.playerSet) {
						if (play.playerName.equals(comps[2])) {
							play.npcX = Float.parseFloat(comps[0]);
							play.npcY = Float.parseFloat(comps[1]);
							System.out.println("Synced player \'" + play.playerName + "\'!");
						}
					}
				}
			}
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
			if (currentLevel != null) {
				for (ClientPlayerNPC npc : currentLevel.playerSet) {
					if (npc.playerName.equals(player)) {
						currentLevel.playerSet.remove(npc);
						break;
					}
				}
			}
			System.out.println("Player list updated: " + currentPlayer.toString());
		}
	}

	/**
	 * update the players rendered. This is in the case of a new conneciton that
	 * hasnt been rendered on the GUI. The logic is still in progress.
	 */
	public void updatePlayers() {
		for (String player : currentPlayer) {
			factory.getCurrentClient().addPlayerToList(player);
		}
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
		canMove = true;
		System.out.println("User can now move...");
		Main.goToMultiplayerState();
		for (String string : currentPlayer) {
			if (!string.equals(thisPlayerName)) {
				removePlayerFromList(string);
				addPlayerToList(string);
			}
		}
	}
	
	public boolean canClientMove() {
		return canMove;
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
	public void sendMovement(KeyBind key, boolean down) {
		// TODO: Move player with given ENUM
		// System.out.println("This is where a player would be moved: " + player + " " +
		// movement);
		if (writer != null) {
			writer.write(" /api/movement/" + key.ordinal() + "_" + down + "\n");
			writer.flush();
		}
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

	public String getThisPlayerName() {
		return thisPlayerName;
	}

	public void setCurrentLevel(Level level) {
		currentLevel = level;
		//currentLevel.playerSet.add(thisPlayer = new ClientPlayerNPC(currentLevel, 128, 128, thisPlayerName));
	}

	public Level getCurrentLevel() {
		return currentLevel;
	}

	public static String getThisMachineIP() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress().toString();
	}

	public ClientPlayerNPC getThisPlayer() {
		return thisPlayer;
	}
	
	public void setThisPlayer(ClientPlayerNPC player) 
	{
		thisPlayer = player;
	}

	public void setGUI(InMultiplayerGameState mpGame) {
		gui = mpGame;
	}

	@Override
	public void run() {
		boolean joined;
		if (socket == null) {
			joined = joinGame();
		} else {
			joined = true;
		}
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

					@SuppressWarnings("rawtypes")
					Map map = response.getMappedResponse();
					String api = (String) map.get("api");
					String key = factory.getPathFromSupported(api);
					if (key == null) {
						System.out.println("Client Does not understand the request: " + api);

					} else {
						NetPacket packet = factory.getAPIRegistry().getOrDefault(key, null);

						if (packet == null) {
							System.out.println("Response not recognized: " + api);
						} else {
							packet.clientExecute(map);
						}
					}

					System.out.printf("Response %s Processed\n", response.getRawReponse());
				}
			}
			System.out.println("\nLost connection with the server goodbye\n");
		} else {
			System.err.println("Currently going to kill the client if it cannot join the server...");
		}
	}

	public void tellClientsToStart() {
		writer.write(" /api/game/started\n");
		writer.flush();
	}

	public void sendRemoval(NPC npc, int ind) 
	{
		writer.write(" /api/cleanup/npcremove/"+ind+"_"+npc.npcX+"_"+npc.npcY+"\n");
		writer.flush();
	}
	
	public void sendLife(NPC npc, int ind, int life) 
	{
		writer.write(" /api/healthsync/"+ind+"_"+npc.npcX+"_"+npc.npcY+"_"+life+"\n");
		writer.flush();
	}

	public void setCanClientMove(boolean move) 
	{
		canMove = move;
	}
}