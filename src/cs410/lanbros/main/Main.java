package cs410.lanbros.main;

import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.state.TestState;
import cs410.lanbros.networkhandler.Factory;

public class Main {

	public static void main(String[] args) {
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
