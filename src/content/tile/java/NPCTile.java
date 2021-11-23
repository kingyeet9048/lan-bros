package content.tile.java;

import java.awt.Graphics2D;

import content.level.java.Level;
import content.npc.java.ClientPlayerNPC;
import content.npc.java.NPC;

public class NPCTile extends Tile
{
	private boolean spawned = false;
	private NPC npc = null;

	public NPCTile(Level level, int x, int y, NPC npc) 
	{
		super(level, x, y);
		this.npc = npc;
	}

	protected NPCTile(Level level) 
	{
		super(level);
	}

	@Override
	public boolean shouldCollideFromSide(TileFace direction) {
		return false;
	}

	@Override
	public String getTileID() {
		return null;
	}

	@Override
	public boolean collideWith(NPC npc) {
		return false;
	}

	@Override
	public void applyCollision(NPC npc) {
		
	}

	@Override
	public void onCollide(NPC npc, TileFace face) {
		
	}

	@Override
	public void updateTile() {
		if(!spawned && npc != null)
		{
			npc.npcX = tileX * TILE_SIZE;
			npc.npcY = tileY * TILE_SIZE;
			
			if(npc instanceof ClientPlayerNPC)
			{
				level.playerSet.add((ClientPlayerNPC)npc);
				System.out.println("SPAWNED PLAYER!!!");
			}
			else
			{
				level.npcSet.add(npc);
			}
			
			spawned = true;
		}
	}

	@Override
	public void renderTile(Graphics2D g) {
		
	}

	@Override
	public Tile createTile(Level level) {
		return null;
	}

}
