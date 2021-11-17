package content.tile.java;

import java.awt.Graphics2D;

import content.npc.java.NPC;

public abstract class Tile 
{
	public int tileX, tileY;
	
	/**
	 * Indicates whether this tile collides with the player or not.
	 */
	protected boolean collideWith;
	
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
