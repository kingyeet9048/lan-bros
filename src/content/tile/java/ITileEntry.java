package content.tile.java;

import content.level.java.Level;
import content.npc.java.NPC;

public interface ITileEntry {
	Tile createTile(Level level);
	String getTileID();
	boolean shouldCollideFromSide(TileFace direction);
	boolean collideWith(NPC npc);
	void applyCollision(NPC npc);
}