package cs410.lanbros.content.npc;

import java.awt.Color;
import java.awt.Graphics2D;

import cs410.lanbros.content.tile.Tile;
import cs410.lanbros.io.KeyBind;
import cs410.lanbros.io.UserInput;

public class PlayerNPC extends NPC
{
	protected int jumpTime;
	
	public PlayerNPC(float x, float y) {
		super(x, y,32,32);
		jumpTime = 0;
	}

	@Override
	protected void updateNPC() 
	{
		if(jumpTime > 0)
		{
			--jumpTime;
			if(jumpTime > 20)
			{
				motionY -= 1.2f+(float)((jumpTime-20)/10.0f);				
			}
		}
		else if(UserInput.isKeyBindPressed(KeyBind.JUMP))
		{
			jumpTime = 40;
		}
		
		if(UserInput.isKeyBindPressed(KeyBind.LEFT))
		{
			motionX -= 1.2f;
		}
		
		if(UserInput.isKeyBindPressed(KeyBind.RIGHT))
		{
			motionX += 1.2f;
		}
	}

	@Override
	public void renderNPC(Graphics2D g) {
		g.setColor(Color.red);
		g.fillOval((int)(npcX-npcWidth/2), (int)(npcY-npcHeight/2), (int)npcWidth, (int)npcHeight);
	}

	@Override
	public void onCollide(Tile tile) {
		
	}

	@Override
	public void onCollide(NPC npc) {
		
	}

}
