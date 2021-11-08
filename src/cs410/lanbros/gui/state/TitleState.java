package cs410.lanbros.gui.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import cs410.lanbros.gui.GuiButton;
import cs410.lanbros.gui.GuiFrame;

public class TitleState extends GuiState
{
	/**
	 * A temporary, faster reference to the size of the GuiFrame.
	 */
	private Rectangle screenSize;
	
	public TitleState(GuiFrame frame)
	{
		super(frame);
		buttons = new GuiButton[]{
				new GuiButton("Singleplayer")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(boolean pressed) {
						frame.addActiveState(new IngameState(frame));
						frame.removeActiveState(TitleState.this);
					}			
				},
				new GuiButton("Join Multiplayer")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(boolean pressed) {
						System.out.println("Pressed join multiplayer!");						
					}
			
				},
				new GuiButton("Host Game")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(boolean pressed) {
						System.out.println("Pressed host game!");
					}
				},
				new GuiButton("Quit")
				{
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(boolean pressed) {
						frame.setVisible(false);
					}
				}
		};
	}

	@Override
	public void renderPre(Graphics2D g) 
	{
		g.setColor(new Color(0,0,20,100));
		g.fillRect(0, 0, screenSize.width, screenSize.height);
	}
	
	@Override
	public void renderPost(Graphics2D g) 
	{
		Font font = g.getFont();
		g.setColor(Color.black);
		this.drawCentered(g, font.deriveFont(50.0f), "LAN Bros!", screenSize.width/2, 100);
	}

	@Override
	public void stateLoaded() {
		screenSize = frame.getBounds();
		addButtonsWithMargin(screenSize.width/2, 200, 15);
	}

	@Override
	public void stateUnloaded() {
		removeButtons();
	}
}
