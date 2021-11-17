package gui.state.java;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import gui.java.GuiButton;
import gui.java.GuiFrame;
import networkhandler.java.Factory;
public class PauseState extends GuiState{
    /**
	 * A temporary, faster reference to the size of the GuiFrame.
	 */
	private Rectangle screenSize;

	public PauseState(GuiFrame frame, Factory factory) {
		super(frame);
		buttons = new GuiButton[] { new GuiButton("Return to Game") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				//TODO:
			}
		}, new GuiButton("Return to Title") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				//TODO
			}

		}};
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
