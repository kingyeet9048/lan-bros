package gui.state.java;

import gui.components.java.GuiButton;
import gui.components.java.GuiFrame;
import networkhandler.shared.java.Factory;

import java.awt.*;

public class TitleState extends GuiState {
	/**
	 * A temporary, faster reference to the size of the GuiFrame.
	 */
	private Rectangle screenSize;

	public TitleState(GuiFrame frame, Factory factory) {
		super(frame);
		buttons = new GuiButton[] { new GuiButton("Singleplayer") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				frame.addActiveState(new IngameState(frame));
				frame.removeActiveState(TitleState.this);
			}
		}, new GuiButton("Join Multiplayer") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				frame.addActiveState(new JoinGameState(frame, factory));
				frame.removeActiveState(TitleState.this);
			}

		}, new GuiButton("Host Game") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				frame.addActiveState(new HostGameState(frame, factory));
				frame.removeActiveState(TitleState.this);
			}
		}, new GuiButton("Set Username") {

			@Override
			public void onClick(boolean pressed) {
				frame.addActiveState(new SetUserNameState(frame, factory));
				frame.removeActiveState(TitleState.this);
			}

		}, new GuiButton("Quit") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				frame.setVisible(false);
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
