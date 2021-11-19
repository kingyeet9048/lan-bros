package content.npc.java;

import java.awt.Graphics2D;

import content.level.java.Level;
import content.tile.java.Tile;
import content.tile.java.TileFace;
import gui.components.java.GuiFrame;
import main.java.Main;

public abstract class NPC {
	public float npcX, npcY, motionX, motionY, npcWidth, npcHeight;
	public TileFace wallHit = null;
	public boolean onGround;
	protected boolean active;
	protected int lifeTime = 0;
	protected Level level;
	private final int[][][] tileNeighborOffsets = { { { -1, 0 }, { 1, 0 } }, { { 0, -1 }, { 0, 1 } } };
	private final double[] tileFloorOffsets = { 1.5, 2 };

	public NPC(Level level, float x, float y, float width, float height) {
		this.level = level;
		npcX = x;
		npcY = y;
		npcWidth = width;
		npcHeight = height;
	}

	public void update() {
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

	protected abstract void updateNPC();

	public abstract void renderNPC(Graphics2D g);

	public abstract void onCollide(Tile tile);

	public abstract void onCollide(NPC npc);
}
