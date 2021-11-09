package cs410.lanbros.content.npc;

import java.awt.Graphics2D;

import cs410.lanbros.content.level.Level;
import cs410.lanbros.content.tile.Tile;
import cs410.lanbros.gui.GuiFrame;

public abstract class NPC {
	public float npcX, npcY, motionX, motionY, npcWidth, npcHeight;
	protected boolean active;
	protected int lifeTime = 0;

	public NPC(float x, float y, float width, float height)
	{
		npcX = x;
		npcY = y;
		npcWidth = width;
		npcHeight = height;
	}
	
	public void update()
	{
		++lifeTime;
		
		//Apply motion
		npcX += motionX *= 0.85f;
		npcY += motionY *= 0.85f;
		
		if(Math.abs(motionX) < 0.025)
		{
			motionX = 0;
		}
		
		if(Math.abs(motionY) < 0.025)
		{
			motionY = 0;
		}
		
		//Clamp NPC to not go below screen
		if(npcY > GuiFrame.SCREEN_HEIGHT-npcHeight*2)
		{
			npcY = GuiFrame.SCREEN_HEIGHT-npcHeight*2;
			motionY = motionY > 0 ? 0 : motionY;
		}
		else if(npcY < GuiFrame.SCREEN_HEIGHT - npcHeight * 3.0)
		{
			motionY += 1.1f;
		}
		
		updateNPC();
	}
	
	protected abstract void updateNPC();
	
	public abstract void renderNPC(Graphics2D g);
	
	public abstract void onCollide(Tile tile);
	
	public abstract void onCollide(NPC npc);
}