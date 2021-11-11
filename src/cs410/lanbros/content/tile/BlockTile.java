package cs410.lanbros.content.tile;

import java.awt.Color;
import java.awt.Graphics2D;

import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.content.npc.NPC;

public class BlockTile extends Tile
{
	public BlockTile() {
		super();
	}
	public BlockTile(int x, int y) {
		super(x,y);
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
	public Tile createTile() {
		return new BlockTile();
	}

	@Override
	public String getTileID() {
		return "B";
	}

	public void renderTile(Graphics2D g) 
	{
		g.scale(Tile.TILE_SIZE, Tile.TILE_SIZE);
		g.setColor(Color.red);
		g.fillRect(tileX,tileY,1,1);
		g.scale(1/Tile.TILE_SIZE,1/Tile.TILE_SIZE);
		g.setColor(Color.black);
		g.drawRect((int)(tileX*Tile.TILE_SIZE),(int)(tileY*Tile.TILE_SIZE),(int)Tile.TILE_SIZE, (int)Tile.TILE_SIZE);
	}
	@Override
	public boolean collideWith(NPC npc) 
	{
		return true;
	}
	@Override
	public void applyCollision(NPC npc) {
		Tile.applyBaseCollide(this, npc);
	}

}
