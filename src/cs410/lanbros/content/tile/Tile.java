package cs410.lanbros.content.tile;

import java.awt.Graphics2D;
import java.util.HashMap;

import cs410.lanbros.content.npc.NPC;

public abstract class Tile implements ITileEntry
{
	public static final float TILE_SIZE = 32.0f;
	private static final HashMap<Class<?extends Tile>, ITileEntry> TILE_REGISTRY = new HashMap<Class<?extends Tile>, ITileEntry>();
	public int tileX, tileY;
	
	protected Tile(){
		tileX = 0;
		tileY = 0;
	}
	
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
	
	/**
	 * Sets the position of this tile to the specified coordinates.
	 * @param x the x value for this position
	 * @param y the y value for this position
	 */
	public void setPosition(int x, int y) 
	{
		tileX = x;
		tileY = y;
	}

	/**
	 * Registers a class to a tileentry to generate tiles for the class. Used for parsing entries from a file
	 * @param tileClass the class to use as a key.
	 * @param entry the ITileEntry to use when generating a level
	 */
	public static void registerTile(Class<?extends Tile> tileClass, ITileEntry entry)
	{
		TILE_REGISTRY.put(tileClass, entry);
		System.out.println("Registered class \'"+tileClass.toString()+"\' to entry \'"+entry.getTileID()+"\'!");
	}
	
	public static Tile fromClass(Class<?extends Tile> tileClass) 
	{
		if(TILE_REGISTRY.containsKey(tileClass))
		{
			return TILE_REGISTRY.get(tileClass).createTile();
		}
		
		return null;
	}
	
	public static Tile fromID(String id)
	{
		for(ITileEntry entry : TILE_REGISTRY.values())
		{
			if(entry.getTileID().equals(id))
			{
				System.out.println("Created tile \'"+entry.getTileID()+"\'!");
				return entry.createTile();
			}
		}
		
		System.out.println("Did not create tile \'"+id+"\'");
		return null;
	}
	
	protected static void applyBaseCollide(Tile tile, NPC npc)
	{
		float xpos = (npc.npcX+npc.motionX)/TILE_SIZE-tile.tileX;
		float ypos = (npc.npcY+npc.motionY)/TILE_SIZE-tile.tileY;
		
		if(xpos < 0.75 && xpos > 0.5)
		{
			System.out.println("XPOS="+xpos + " and was VALID");
			npc.npcX=tile.tileX*TILE_SIZE+npc.npcWidth;
		}
		else if(xpos > 0.25 && xpos < 0.5)
		{
			System.out.println("XPOS="+xpos + " and was VALID");
			npc.npcX=tile.tileX*TILE_SIZE-npc.npcWidth;
		}
		
		if(ypos < -0.5 && ypos < 0)
		{
			System.out.println("YPOS="+ypos + " and was VALID");
			npc.npcY=tile.tileY*TILE_SIZE-npc.npcHeight;
			npc.motionY = npc.motionY < 0 ? npc.motionY : 0;
			npc.onGround = true;
		}
	}
	
	//Register the tile 
	static {
		Tile.registerTile(BlockTile.class, new BlockTile());
	}
}
