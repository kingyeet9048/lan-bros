package cs410.lanbros.gui.state;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import cs410.lanbros.animation.SpriteSheet;
import cs410.lanbros.gui.GuiButton;
import cs410.lanbros.gui.GuiFrame;

public class TestState extends GuiState
{
	private GuiButton button;

	private GuiButton titleButton;

	private SpriteSheet test;

	public TestState(GuiFrame frame)
	{
		super(frame);
		
		button = new GuiButton("Testing!") {
			private static final long serialVersionUID = 0;

			public void onClick(boolean pressed) {
				System.out.println("Button pressed? " + pressed);
			}
		};
		
		titleButton = new GuiButton("Go To Title") {
			private static final long serialVersionUID = 0;

			public void onClick(boolean pressed) {
				frame.removeActiveState(TestState.this);
				frame.addActiveState(new TitleState(frame));
			}
		};
		
		test = new SpriteSheet(new ImageIcon("resources/gfx/test.png"))
				.addFrame("wink0", 15, 0, 0, 8, 8).addFrame("wink1", 10, 8, 0, 8, 8).addFrame("wink2", 15, 16, 0, 8, 8)
				.addAnimation("wink", "wink0", "wink1", "wink2", "wink1");
	}

	@Override
	public void renderPost(Graphics2D g)
	{
		test.updateSpriteSheet();
		test.renderSpriteSheet((Graphics2D)g, 64, 64, 4.0f, 4.0f, (float)Math.toRadians(System.nanoTime() / 2000.0d),0,0);
	}
	
	@Override
	public void stateLoaded()
	{
		button.setBounds(160, 66, 128, 32);
		titleButton.setBounds(160, 100, 128, 32);
		frame.getActivePanel().add(button);
		frame.getActivePanel().add(titleButton);
	}

	@Override
	public void stateUnloaded()
	{
		frame.getActivePanel().remove(button);
		frame.getActivePanel().remove(titleButton);
	}

	@Override
	public void renderPre(Graphics2D g) {
		g.setColor(Color.gray);
		g.fillRect(0, 0, frame.getBounds().width, frame.getBounds().height);
	}
}
