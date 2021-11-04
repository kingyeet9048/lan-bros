package cs410.lanbros.main;

import javax.swing.ImageIcon;

import cs410.lanbros.animation.SpriteSheet;
import cs410.lanbros.gui.GuiButton;
import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.state.TestState;

public class Main {
	public static final SpriteSheet test = new SpriteSheet(new ImageIcon("resources/gfx/test.png"))
			.addFrame("wink0", 15, 0, 0, 8, 8).addFrame("wink1", 10, 8, 0, 8, 8).addFrame("wink2", 15, 16, 0, 8, 8)
			.addAnimation("wink", "wink0", "wink1", "wink2", "wink1");

	public static void main(String[] args) {
		GuiFrame frame = new GuiFrame();
		frame.addActiveState(new TestState());

		GuiButton button = new GuiButton("Testing!") {
			private static final long serialVersionUID = 0;

			public void onClick(boolean pressed) {
				System.out.println("Button pressed? " + pressed);
			}
		};
		button.setBounds(160, 66, 128, 32);
		frame.getActivePanel().add(button);

		new Thread(() -> {
			while (!frame.isClosed()) {
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
