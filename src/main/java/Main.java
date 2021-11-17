package main.java;

import cs410.lanbros.content.level.Level;
import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.content.npc.ServerPlayerNPC;
import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.state.InMultiplayerGameState;
import cs410.lanbros.gui.state.TestState;
import cs410.lanbros.networkhandler.Factory;
import cs410.lanbros.networkhandler.Client.Client;
import cs410.lanbros.networkhandler.Server.Server;

public class Main {
	private static Factory factory = new Factory();
	private static GuiFrame frame = new GuiFrame();

	public static void main(String[] args) {

		factory.setPort(4321);
		factory.setMAX_PLAYERS(5);
		factory.makeBaseAPIRegistry();
		Server server = factory.makeServer();
		Thread serveThread = new Thread(server);
		serveThread.start();

		frame.addActiveState(new TestState(frame, factory));

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
			level.playerSet.add(new ServerPlayerNPC(3, 3, playerName));
		}
	}
}
