package cs410.lanbros.gui.state;

import java.awt.Graphics2D;

import cs410.lanbros.main.Main;

public class TestState implements GuiState
{
	@Override
	public void render(Graphics2D g)
	{
		Main.test.updateSpriteSheet();
		Main.test.renderSpriteSheet((Graphics2D)g, 64, 64, 4.0f, 4.0f, (float)Math.toRadians(System.nanoTime() / 2000.0d),0,0);
	}
}
