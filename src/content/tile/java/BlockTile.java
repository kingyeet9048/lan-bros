package content.tile.java;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import animation.java.SpriteSheet;
import content.level.java.Level;
import content.npc.java.NPC;

public class BlockTile extends Tile
{
	public static final SpriteSheet TILE_SPRITE = new SpriteSheet(new ImageIcon("resources/gfx/tile.png"))
			.addFrame("top", 10000, 0, 10, 10, 10)
			.addFrame("dirt", 10000, 0, 0, 10, 10)
			.addAnimation("top", "top")
			.addAnimation("dirt", "dirt");

	public BlockTile(Level level) {
		super(level);
	}
	public BlockTile(Level level, int x, int y) {
		super(level, x,y);
	}

	@Override
	public void onCollide(NPC npc, TileFace face) 
	{
	}

	@Override
	public void updateTile() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Tile createTile(Level level) {
		return new BlockTile(level);
	}

	@Override
	public String getTileID() {
		return "B";
	}

	public void renderTile(Graphics2D g) 
	{
		TILE_SPRITE.setCurrentAnimation(tileY == level.getHeightMap()[tileX] ? "top" : "dirt");
		TILE_SPRITE.renderSpriteSheet(g, (int) (tileX * TILE_SIZE + TILE_SIZE/2), (int) (tileY * TILE_SIZE + TILE_SIZE/2), 3.0f, 3.0f);
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
