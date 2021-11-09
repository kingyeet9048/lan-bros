package cs410.lanbros.main;

import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.state.InMultiplayerGameState;
import cs410.lanbros.gui.state.JoinGameState;
import cs410.lanbros.gui.state.TestState;
import cs410.lanbros.networkhandler.Factory;
import cs410.lanbros.networkhandler.Client.Client;
import cs410.lanbros.networkhandler.Server.Server;

public class Main {
	private static Factory factory = new Factory();
	private static GuiFrame frame = new GuiFrame();
	
	public static void main(String[] args) {

		Server server = new Server(4321, 4);
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
	
	public static boolean startClient(String serverAddress)
	{
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
	
	public static Factory getNetworkFactory()
	{
		return factory;
	}
}
