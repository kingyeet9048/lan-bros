package cs410.lanbros.main;

import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.state.TestState;
import cs410.lanbros.networkhandler.Factory;
import cs410.lanbros.networkhandler.Server.Server;

public class Main {

	public static void main(String[] args) {

		Server server = new Server(4321, 4);
		Thread serveThread = new Thread(server);
		serveThread.start();

		Factory factory = new Factory();
		GuiFrame frame = new GuiFrame();
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
}
