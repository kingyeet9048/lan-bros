package main.java;

import java.io.IOException;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import content.npc.java.ServerPlayerNPC;
import gui.components.java.GuiFrame;
import gui.state.java.InMultiplayerGameState;
import gui.state.java.PauseState;
import gui.state.java.SetUserNameState;
import gui.state.java.TitleState;
import networkhandler.client.java.Client;
import networkhandler.server.java.Server;
import networkhandler.shared.java.Factory;

public class Main {
	private static Factory factory = new Factory();
	private static GuiFrame frame = new GuiFrame();
	private static Server server;
	private static PauseState pauseState;

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
			return true;
		}

		return false;
	}

	public static void goToMultiplayerState() {
		frame.wipeActiveStates();
		frame.addActiveState(factory.getJoinedGameState());
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

	public static void pauseGame(String player) {
		pauseState = factory.makePauseState(frame);
		pauseState.playerThatPaused = player;
		frame.addActiveState(pauseState);
		factory.getCurrentClient().setCanClientMove(false);
		System.out.println("Game Paused...");
	}

	public static void unPauseGame() {
		frame.removeActiveState(pauseState);
		factory.setPauseState(new PauseState(frame, factory));
		factory.getCurrentClient().setCanClientMove(true);

	}

	public static void returnToTitle() {
		try {
			if (factory.getCurrentClient().isHost()) {
				factory.getCurrentServer().getServer().close();
			}
			factory.getCurrentClient().getSocket().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		factory.setPauseState(new PauseState(frame, factory));
		frame.wipeActiveStates();
		frame.addActiveState(new TitleState(frame, factory));
	}
}
