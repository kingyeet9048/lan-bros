package main.java;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import content.npc.java.ServerPlayerNPC;
import gui.components.java.GuiFrame;
import gui.state.java.InMultiplayerGameState;
import gui.state.java.SetUserNameState;
import networkhandler.client.java.Client;
import networkhandler.server.java.Server;
import networkhandler.shared.java.Factory;

public class Main {
	private static Factory factory = new Factory();
	private static GuiFrame frame = new GuiFrame();
	private static Server server;

	public static void main(String[] args) {
		factory.startFactory();
		frame.addActiveState(new SetUserNameState(frame, factory));

		new Thread(() -> {
			while (frame.isVisible()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					break;
				}
			}
			System.exit(0);
		}).start();
	}

	public static boolean startClient(String serverAddress) {
		factory.setServerAddress(serverAddress);
		factory.setHost(false);
		Client currentClient = factory.makeClient();

		if (currentClient.joinGame() == true) {
			Thread clientThread = new Thread(currentClient);
			clientThread.start();
			InMultiplayerGameState mpGame = factory.makeGameState(frame, currentClient.getThisPlayerName());
			currentClient.setGUI(mpGame);
			frame.wipeActiveStates();
			frame.addActiveState(mpGame);
			return true;
		}

		return false;
	}

	public static Factory getNetworkFactory() {
		return factory;
	}

	public static void addNewPlayer(String playerName) {
		boolean playerLoaded = false;
		Level level = factory.getCurrentClient().getCurrentLevel();
		for (ClientPlayerNPC player : level.playerSet) {
			if (player.playerName.equals(playerName)) {
				playerLoaded = true;
				break;
			}
		}
		if (!playerLoaded) {
			level.playerSet.add(new ServerPlayerNPC(level, 3, 3, playerName));
		}
	}

	public static void startServer() {
		server = factory.makeServer();
		Thread serveThread = new Thread(server);
		serveThread.start();
	}
}
