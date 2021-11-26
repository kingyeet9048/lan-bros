package gui.state.java;

import animation.java.SpriteSheet;
import gui.components.java.GuiButton;
import gui.components.java.GuiFrame;
import gui.components.java.GuiInput;
import networkhandler.shared.java.Factory;

import javax.swing.*;
import java.awt.*;

public class UsernameState extends GuiState {

	private SpriteSheet test;

	private Rectangle screenSize;

	public UsernameState(GuiFrame frame, Factory factory) {
		super(frame);

		buttons = new GuiButton[] { new GuiButton("Go To Title") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onClick(boolean pressed) {
				if (pressed) {
					String username = inputs[0].getText();
					if (isValidUsername(username.replaceAll("\\s+", ""))) {
						factory.setPlayerUsername(username);
						frame.addActiveState(new TitleState(frame, factory));
						frame.removeActiveState(UsernameState.this);
					} else {
						JOptionPane.showMessageDialog(frame,
								"Your username cannot be 'username' or nothing. Additionally, your username has to be 10 characters or shorter and cannot have the following special cases: . , / _");
					}
				}
			}

		}, };

		inputs = new GuiInput[] { new GuiInput("username") {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
		}, };

		test = new SpriteSheet(new ImageIcon("resources/gfx/test.png")).addFrame("wink0", 15, 0, 0, 8, 8)
				.addFrame("wink1", 10, 8, 0, 8, 8).addFrame("wink2", 15, 16, 0, 8, 8)
				.addAnimation("wink", "wink0", "wink1", "wink2", "wink1");
	}

	@Override
	public void renderPost(Graphics2D g) {
		test.updateSpriteSheet();
		test.renderSpriteSheet((Graphics2D) g, 64, 64, 4.0f, 4.0f, (float) Math.toRadians(System.nanoTime() / 2000.0d),
				0, 0);
		Font font = g.getFont();
		g.setColor(Color.black);
		this.drawCentered(g, font.deriveFont(50.0f), "LAN Bros!", screenSize.width / 2, 100);
		this.drawCentered(g, font.deriveFont(30.0f), "Enter a username you would like to play as!",
				screenSize.width / 2, 200);
	}

	@Override
	public void stateLoaded() {
		// TODO Auto-generated method stub
		screenSize = frame.getBounds();
		addInputWithMargin(screenSize.width / 2, 200, 15);
		addButtonsWithMargin(screenSize.width / 2, 200 + 100, 15);
	}

	@Override
	public void stateUnloaded() {
		removeButtons();
		removeInputs();
	}

	@Override
	public void renderPre(Graphics2D g) {
		g.setColor(new Color(0, 0, 20, 100));
		g.fillRect(0, 0, screenSize.width, screenSize.height);
	}

	public boolean isValidUsername(String username) {
		if (!username.equals("username") && !username.equals("") && !username.contains("_") && !username.contains(",")
				&& !username.contains(".") && !username.contains("/") && username.length() <= 10) {
			return true;
		}
		return false;
	}
}
