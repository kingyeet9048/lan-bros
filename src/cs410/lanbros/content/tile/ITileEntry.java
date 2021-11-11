package cs410.lanbros.content.tile;

import cs410.lanbros.content.npc.NPC;

public interface ITileEntry {
	Tile createTile();
	String getTileID();
	boolean collideWith(NPC npc);
	void applyCollision(NPC npc);
}
