package content.npc.java;

import java.awt.Graphics2D;
import java.util.HashMap;

import content.level.java.Level;
import content.tile.java.ITileEntry;
import content.tile.java.Tile;
import content.tile.java.TileFace;
import gui.components.java.GuiFrame;
import main.java.Main;

public abstract class NPC implements ITileEntry 
{
	private static final HashMap<Class<?extends NPC>, ITileEntry> NPC_REGISTRY = new HashMap<Class<?extends NPC>, ITileEntry>();
	private static final int DMG_COOLDOWN = 20;
	
	public float npcX, npcY, motionX, motionY, npcWidth, npcHeight;
	public TileFace wallHit = null;
	public boolean onGround;
	protected boolean active;
	protected int lifeTime = 0;
	protected Level level;
	private final int[][][] tileNeighborOffsets = { { { -1, 0 }, { 1, 0 } }, { { 0, -1 }, { 0, 1 } } };
	private final double[] tileFloorOffsets = { 1.5, 2 };
	protected int lifeMax = 10, life = 10, dmgCooldown = 0;

	public NPC(Level level, float x, float y, float width, float height) {
		this.level = level;
		npcX = x;
		npcY = y;
		npcWidth = width;
		npcHeight = height;
	}
	
	public void update()
	{
		if(life <= 0) {
			return;
		}
		
		++lifeTime;

		// Apply motion
		npcX += motionX *= 0.85f;
		npcY += motionY *= 0.85f;

		if (wallHit == TileFace.RIGHT && motionX < 0) {
			wallHit = null;
		} else if (wallHit == TileFace.LEFT && motionX > 0) {
			wallHit = null;
		}

		if (Math.abs(motionX) < 0.025) {
			motionX = 0;
		}

		if (Math.abs(motionY) < 0.025) {
			motionY = 0;
		}

		// Clamp NPC to not go below screen
		if (npcY > GuiFrame.SCREEN_HEIGHT - npcHeight * 2) {
			npcY = GuiFrame.SCREEN_HEIGHT - npcHeight * 2;
			motionY = motionY > 0 ? 0 : motionY;
		} else if (npcY < GuiFrame.SCREEN_HEIGHT - npcHeight * 3.0) {
			if (!onGround) {
				motionY += 1.1f;
			}
		}
		
		if(dmgCooldown > 0)
		{
			--dmgCooldown;
		}
		
		updateTileCollision();
		updateNPC();
	}

	protected void updateTileCollision() {
		int tX = (int) (npcX / Tile.TILE_SIZE);
		int tY = (int) (npcY / Tile.TILE_SIZE);
		Tile[][] tileMap = level.getTileMap();

		if (tX < tileMap.length) {
			boolean packetFlag = false;
			boolean collided = false;
			for (int[][] offSets : tileNeighborOffsets) {
				for (int[] off : offSets) {
					int offX = tX + off[0], offY = tY + off[1];
					if (offX >= 0 && offX < tileMap.length && offY >= 0 && offY < tileMap[offX].length) {
						if (tileMap[offX][offY] != null && tileMap[offX][offY].collideWith(this)) {
							tileMap[offX][offY].applyCollision(this);
							collided = true;
							packetFlag = true;
							// System.out.println("\t|\tCollided with tile
							// "+((ITileEntry)(tileMap[offX][offY])).getTileID());
							break;
						}
					}
				}
			}

			if (!collided) {
				wallHit = null;
			}

			onGround = false;

			boolean airFound = false;
			for (double off : tileFloorOffsets) {
				Tile lowerTile = tileMap[tX][(int) Math.min(npcY / Tile.TILE_SIZE + off, tileMap[tX].length - 1)];
				if (lowerTile != null && lowerTile.shouldCollideFromSide(TileFace.TOP)) {
					if (!airFound) {
						onGround = true;
						packetFlag = true;
					}
					break;
				} else {
					airFound = true;
				}
			}

			if (packetFlag && collided == true) {
				if (Main.getNetworkFactory().getCurrentClient() != null) {
					// Main.getNetworkFactory().getCurrentClient().updateClientPlayerPosition();
				}
			}
		} else {
			onGround = false;
		}
	}
	
	protected void setMaxLife(int health)
	{
		lifeMax = life = health;
	}
	
	public int getMaxLife()
	{
		return lifeMax;
	}
	
	public int getLife()
	{
		return life;
	}
	
	public void setLife(int health)
	{
		life = health;
	}
	
	public void damageNPC(int amount)
	{
		if(dmgCooldown <= 0)
		{
			setLife(getLife()-amount);
			dmgCooldown = DMG_COOLDOWN;
			int ind = level.npcSet.indexOf(this);
			
			if(ind == -1)
			{
				ind = level.playerSet.indexOf(this);
			}
			
			if(Main.getNetworkFactory().getCurrentClient() != null)
				Main.getNetworkFactory().getCurrentClient().sendLife(this, ind, life);
			
			if (life <= 0)
			{
				onKill();
			}			
		}
	}
	
	protected synchronized void onKill()
	{
		onNPCKill();
		int ind = level.npcSet.indexOf(this);
		
		if(ind == -1)
		{
			ind = level.playerSet.indexOf(this);
		}
		
		if(Main.getNetworkFactory().getCurrentClient() != null)
		{
			Main.getNetworkFactory().getCurrentClient().sendRemoval(this, ind);			
		}
		else
		{
			level.queueForRemoval(this);
		}
	}
	
	protected abstract void onNPCKill();
	
	protected abstract void updateNPC();

	public abstract void renderNPC(Graphics2D g);

	
	public abstract void onCollide(Tile tile, TileFace face);
	public abstract void onCollide(NPC npc);
	
	
	public static Tile fromID(Level level, String id)
	{
		for(ITileEntry entry : NPC_REGISTRY.values())
		{
			if(entry.getTileID().equals(id))
			{
				System.out.println("Created tile \'"+entry.getTileID()+"\'!");
				return entry.createTile(level);
			}
		}
		
		System.out.println("Did not create tile \'"+id+"\'");
		return null;
	}
	
	static {
		NPC_REGISTRY.put(ClientPlayerNPC.class, new ClientPlayerNPC(null, 0, 0, null));
		NPC_REGISTRY.put(SpikeBallNPC.class, new SpikeBallNPC(null, 0, 0));
	}
}
