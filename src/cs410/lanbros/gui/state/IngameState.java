package cs410.lanbros.gui.state;

import java.awt.Graphics2D;

import cs410.lanbros.content.level.Level;
import cs410.lanbros.content.npc.PlayerNPC;
import cs410.lanbros.gui.GuiFrame;

public class IngameState extends GuiState
{
	public Level currentLevel;
	
	public IngameState(GuiFrame frame) 
	{
		super(frame);
	}

	@Override
	public void renderPre(Graphics2D g) 
	{
		currentLevel.updateLevel();
		currentLevel.renderLevel(g);
	}

	@Override
	public void renderPost(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stateLoaded() {
		currentLevel = new Level();
		currentLevel.playerSet.add(new PlayerNPC(3,3));
		System.out.println("Joined singleplayer!");

	}

	@Override
	public void stateUnloaded() {
		
	}

}
