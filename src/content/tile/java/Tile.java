package content.tile.java;

import java.awt.Graphics2D;
import java.util.HashMap;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import content.npc.java.NPC;
import main.java.Main;

public abstract class Tile implements ITileEntry {
	public static final float TILE_SIZE = 30.0f;
	private static final HashMap<Class<? extends Tile>, ITileEntry> TILE_REGISTRY = new HashMap<Class<? extends Tile>, ITileEntry>();
	public int tileX, tileY;
	protected final Level level;

	protected Tile(Level level) {
		this.level = level;
		tileX = 0;
		tileY = 0;
	}

	public Tile(Level level, int x, int y) {
		this.level = level;
		tileX = x;
		tileY = y;
	}

	/**
	 * When an NPC collides with this tile, this is called.
	 * @param face the face collided on
	 */
	public abstract void onCollide(NPC npc, TileFace face);
	
	/**
	 * Updates the tile.
	 */
	public abstract void updateTile();

	/**
	 * Renders the tile.
	 * 
	 * @param g
	 */
	public abstract void renderTile(Graphics2D g);

	/**
	 * Sets the position of this tile to the specified coordinates.
	 * 
	 * @param x the x value for this position
	 * @param y the y value for this position
	 */
	public void setPosition(int x, int y) {
		tileX = x;
		tileY = y;
	}

	/**
	 * Registers a class to a tileentry to generate tiles for the class. Used for
	 * parsing entries from a file
	 * 
	 * @param tileClass the class to use as a key.
	 * @param entry     the ITileEntry to use when generating a level
	 */
	public static void registerTile(Class<? extends Tile> tileClass, ITileEntry entry) {
		TILE_REGISTRY.put(tileClass, entry);
		System.out.println("Registered class \'" + tileClass.toString() + "\' to entry \'" + entry.getTileID() + "\'!");
	}
	public static Tile fromClass(Level level, Class<? extends Tile> tileClass) {
		if (TILE_REGISTRY.containsKey(tileClass)) {
			return TILE_REGISTRY.get(tileClass).createTile(level);
		}

		return null;
	}

	public static Tile fromID(Level level, String id) {
		for (ITileEntry entry : TILE_REGISTRY.values()) {
			if (entry.getTileID().equals(id)) {
				// System.out.println("Created tile \'"+entry.getTileID()+"\'!");
				return entry.createTile(level);
			}
		}

		// System.out.println("Did not create tile \'"+id+"\'");
		return null;
	}

	public boolean shouldCollideFromSide(TileFace direction) {
		return true;
	}

	protected static void applyBaseCollide(Tile tile, NPC npc) {
		if (tile == null || npc == null) {
			return;
		}

		float xpos = (npc.npcX + npc.motionX) / TILE_SIZE - tile.tileX;
		float ypos = (npc.npcY) / TILE_SIZE - tile.tileY;
		int[] heightMap = tile.level.getHeightMap();


		if(xpos < 0.9 && xpos > 0.65 && tile.shouldCollideFromSide(TileFace.RIGHT))
		{
			if(npc.motionX < 0)
			{
				npc.onCollide(tile, TileFace.RIGHT);
				tile.onCollide(npc, TileFace.RIGHT);

				npc.motionX = 0;
				npc.wallHit = TileFace.RIGHT;
				
				if(xpos > 0.7)
					npc.npcX = tile.tileX * TILE_SIZE + TILE_SIZE/2.0f + npc.npcWidth/2.0f;
				
				if(ypos > 1)
				{

					int height = heightMap[Math.max(tile.tileX-1, 0)];
					
					if(height - tile.tileY < 1 && height - tile.tileY > 0)
					{
						npc.npcY = height * TILE_SIZE - npc.npcHeight;
						npc.onGround = true;
					}
				}
			}
		}
		else if(xpos > 0.1 && xpos < 0.35 && tile.shouldCollideFromSide(TileFace.LEFT))
		{
			if(npc.motionX > 0)
			{
				npc.onCollide(tile, TileFace.LEFT);
				tile.onCollide(npc, TileFace.LEFT);
				//npc.npcX = tile.tileX * TILE_SIZE + TILE_SIZE/2.0f - npc.npcWidth/2.0f;
				npc.motionX = 0;
				npc.wallHit = TileFace.LEFT;

				if (xpos < 0.3)
					npc.npcX = tile.tileX * TILE_SIZE + TILE_SIZE / 2.0f - npc.npcWidth / 2.0f;

				if (ypos > 1) {
					int height = heightMap[Math.min(tile.tileX + 1, heightMap.length)];

					if (height - tile.tileY < 1 && height - tile.tileY > 0) {
						npc.npcY = height * TILE_SIZE - npc.npcHeight;
						npc.onGround = true;
					}

				}
			}
		}
	
		if(ypos < -0.5 && ypos < 0 && tile.shouldCollideFromSide(TileFace.TOP))
		{
			npc.onCollide(tile, TileFace.TOP);
			tile.onCollide(npc, TileFace.TOP);

			if(npc.motionY > 0)
			{
				npc.npcY = tile.tileY * TILE_SIZE - npc.npcHeight;
				npc.motionY = 0;
			}

			npc.onGround = true;
		}
	}

	// Register the tile
	static {
		Tile.registerTile(BlockTile.class, new BlockTile(null));
	}
}
