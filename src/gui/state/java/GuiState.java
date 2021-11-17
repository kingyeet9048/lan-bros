package gui.state.java;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import gui.components.java.GuiButton;
import gui.components.java.GuiFrame;
import gui.components.java.GuiInput;

/**
 * An abstract class to handle rendering a state of the game. Intended to
 * simplify layering menus over other rendered elements.
 * 
 * @author Ashton Schultz
 *
 */
public abstract class GuiState {
	/**
	 * The array of buttons present in this state.
	 */
	protected GuiButton[] buttons;

	/**
	 * The array of inputs present in the this state.
	 */
	protected GuiInput[] inputs;

	/**
	 * A reference to the GuiFrame that this State is rendered in.
	 */
	protected final GuiFrame frame;

	public GuiState(GuiFrame frame) {
		this.frame = frame;
	}

	/**
	 * Called when this state is active from {@link GuiFrame}.
	 * 
	 * @param g the graphics object to render to, before rendering the active
	 *          JPanel.
	 */
	public abstract void renderPre(Graphics2D g);

	/**
	 * Called when this state is active from {@link GuiFrame}.
	 * 
	 * @param g the graphics object to render to, after rendering the active JPanel.
	 */
	public abstract void renderPost(Graphics2D g);

	/**
	 * Called when this state is loaded into the GuiFrame. Use it to add ui elements
	 * to the frame's active panel.
	 */
	public abstract void stateLoaded();

	/**
	 * Called when this state is unloaded from the GuiFrame. Use it to remove ui
	 * elements from the frame's active panel.
	 */
	public abstract void stateUnloaded();

	/**
	 * Loads all buttons in this GuiState in a list, with
	 * 
	 * @param x       the top-left x-coordinate for the list of buttons to render
	 *                at.
	 * @param y
	 * @param spacing the pixels to space each button by, including their respective
	 *                heights
	 */
	protected void addButtonsWithMargin(int x, int y, int spacing) {
		int curY = y;
		for (int i = 0; i < buttons.length; ++i) {
			int width = buttons[i].getButtonWidth();
			buttons[i].setBounds(x - width / 2, curY, width, buttons[i].getButtonHeight());
			frame.getActivePanel().add(buttons[i]);
			curY += buttons[i].getButtonHeight() + spacing;
		}
	}

	/**
	 * Loads all inputs in this GuiState in a list, with
	 * 
	 * @param x       the top-left x-coordinate for the list of inputs to render at.
	 * @param y
	 * @param spacing the pixels to space each input by, including their respective
	 *                heights
	 */
	protected void addInputWithMargin(int x, int y, int spacing) {
		int curY = y;
		for (int i = 0; i < inputs.length; ++i) {
			int width = inputs[i].getInputWidth();
			inputs[i].setBounds(x - width / 2, curY, width, inputs[i].getInputHeight());
			frame.getActivePanel().add(inputs[i]);
			curY += inputs[i].getInputHeight() + spacing;
		}
	}

	/**
	 * Removes all buttons in this GuiState from the active JPanel in the GuiFrame.
	 */
	protected void removeButtons() {
		for (GuiButton button : buttons) {
			frame.getActivePanel().remove(button);
			button.setVisible(false);
		}
	}

	/**
	 * Removes all inputs in the GuiState from the active JPanel in the GuiFrame.
	 */
	protected void removeInputs() {
		for (GuiInput input : inputs) {
			frame.getActivePanel().remove(input);
			input.setVisible(false);
		}
	}

	/**
	 * Draws the given string centered on the specified coordinates.
	 * 
	 * @param g    the graphics object to use for drawing
	 * @param font the font to use
	 * @param str  the string to draw
	 * @param x    the x coordinate
	 * @param y    the y coordinate
	 */
	protected void drawCentered(Graphics g, Font font, String text, float x, float y) {
		// Get the FontMetrics
		FontMetrics metrics = g.getFontMetrics(font);
		// Determine the X coordinate for the text
		x -= metrics.stringWidth(text) / 2;
		// Determine the Y coordinate for the text (note we add the ascent, as in java
		// 2d 0 is top of the screen)
		y -= metrics.getAscent() / 2;
		// Draw the String
		g.setFont(font);
		g.drawString(text, (int) x, (int) y);
		// Source:
		// https://stackoverflow.com/questions/27706197/how-can-i-center-graphics-drawstring-in-java
	}
}
