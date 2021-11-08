package cs410.lanbros.content.tile;

import java.awt.Graphics2D;

import cs410.lanbros.content.npc.NPC;

public abstract class Tile 
{
	public int tileX, tileY;
	
	public Tile(int x, int y)
	{
		tileX = x;
		tileY = y;
	}
	
	/**
	 * When an NPC collides with this tile, this is called.
	 */
	public abstract void onCollide(NPC npc);
	
	/**
	 * Updates the tile.
	 */
	public abstract void updateTile();
	
	/**
	 * Renders the tile.
	 * @param g
	 */
	public abstract void renderTile(Graphics2D g);
}
