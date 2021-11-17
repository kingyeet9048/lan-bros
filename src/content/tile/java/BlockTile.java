package content.tile.java;

import java.awt.Graphics2D;

import content.npc.java.NPC;

public class BlockTile extends Tile
{
	public BlockTile(int x, int y) {
		super(x, y);
	}

	@Override
	public void onCollide(NPC npc) 
	{
		boolean left = npc.npcX < tileX + 16;
		boolean top = npc.npcY < tileY + 16;
		
		if(top)
		{
			npc.npcY = tileY;
			npc.motionY = 0;
		}
		else
		{
			if(left)
			{
				npc.npcX = tileX-32;
				npc.motionX = npc.motionX > 0 ? 0 : npc.motionX;
			}
			else
			{				
				npc.npcX = tileX+32;
				npc.motionX = npc.motionX < 0 ? 0 : npc.motionX;
			}
		}
	}

	@Override
	public void updateTile() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void renderTile(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

}
