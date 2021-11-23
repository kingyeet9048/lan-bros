package gui.state.java;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import animation.java.SpriteSheet;
import gui.components.java.GuiButton;
import gui.components.java.GuiFrame;
import gui.components.java.GuiInput;
import networkhandler.shared.java.Factory;

public class SetUserNameState extends GuiState {

	private SpriteSheet test;

	private Rectangle screenSize;

	public SetUserNameState(GuiFrame frame, Factory factory) {
		super(frame);

		buttons = new GuiButton[] { new GuiButton("Go To Title") {

			@Override
			public void onClick(boolean pressed) {
				if (pressed) {
					String username = inputs[0].getText();
					if (!username.equals("username") && !username.equals("")) {
						factory.setPlayerUsername(username);
						frame.addActiveState(new TitleState(frame, factory));
						frame.removeActiveState(SetUserNameState.this);
					} else {
						JOptionPane.showMessageDialog(frame, "Enter a username that is not 'username' and not null.");
					}
				}
			}

		}, };

		inputs = new GuiInput[] { new GuiInput("username") {
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
}
