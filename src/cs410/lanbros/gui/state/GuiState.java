package cs410.lanbros.gui.state;

import java.awt.Graphics2D;

/**
 * An interface to handle rendering a state of the game. Intended to simplify layering menus over other rendered elements.
 * @author Ashton Schultz
 *
 */
public interface GuiState {
	/**
	 * Called when this state is active from {@link GuiFrame}.
	 * @param g the graphics object to render to.
	 */
	void render(Graphics2D g);
}
