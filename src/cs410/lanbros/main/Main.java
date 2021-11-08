package cs410.lanbros.main;

import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.state.TestState;

public class Main {

	public static void main(String[] args) {
		GuiFrame frame = new GuiFrame();
		frame.addActiveState(new TestState(frame));

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
