package gui.state.java;

import gui.components.java.GuiButton;
import gui.components.java.GuiFrame;
import io.java.KeyBind;
import main.java.Main;
import networkhandler.shared.java.Factory;

import java.awt.*;

public class PauseState extends GuiState {
	/**
	 * A temporary, faster reference to the size of the GuiFrame.
	 */
	private Rectangle screenSize;
	public String playerThatPaused = "";

	public PauseState(GuiFrame frame, Factory factory) {
		super(frame);
		buttons = new GuiButton[] { new GuiButton("Return to Game") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				if (playerThatPaused.equals(factory.getCurrentClient().getThisPlayerName())) {
					Main.unPauseGame();
					factory.getCurrentClient().sendMovement(KeyBind.PAUSE, false);
				} else {
					System.out.println("You are not the player that paused the game...");
				}
			}
		}, new GuiButton("Return to Title") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				factory.getCurrentClient().sendMovement(KeyBind.PAUSE, false);
				if (factory.getCurrentClient().isHost()) {
					// tell client to send api to end game...
				}
				Main.returnToTitle();
			}

		} };
	}

	@Override
	public void renderPre(Graphics2D g) {
		g.setColor(new Color(0, 0, 20, 100));
		g.fillRect(0, 0, screenSize.width, screenSize.height);
	}

	@Override
	public void renderPost(Graphics2D g) {
		Font font = g.getFont();
		g.setColor(Color.black);
		this.drawCentered(g, font.deriveFont(50.0f), "LAN Bros!", screenSize.width / 2, 100);
		this.drawCentered(g, font.deriveFont(30.0f), "Game paused by: " + playerThatPaused, screenSize.width / 2, 150);
	}

	@Override
	public void stateLoaded() {
		screenSize = frame.getBounds();
		addButtonsWithMargin(screenSize.width / 2, 200, 15);
	}

	@Override
	public void stateUnloaded() {
		removeButtons();
	}
}
