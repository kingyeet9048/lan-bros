package content.npc.java;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import animation.java.SpriteSheet;
import content.level.java.Level;
import content.tile.java.Tile;
import content.tile.java.TileFace;
import io.java.KeyBind;
import io.java.UserInput;

public class ClientPlayerNPC extends NPC {
	public static final SpriteSheet PLAYER_SPRITE = new SpriteSheet(new ImageIcon("resources/gfx/player_Chef1.png"));
	protected int jumpTime;
	public String playerName;
	public boolean canMove = false;

	public ClientPlayerNPC(Level level, float x, float y, String playerName) {
		super(level, x, y, 32, 32);
		jumpTime = 0;
		this.playerName = playerName;
	}

	@Override
	protected void updateNPC() {
		handleMovement();
	}

	protected void handleMovement() {
		if (canMove) {
			if (jumpTime > 0) {
				--jumpTime;
				onGround = false;
				if (jumpTime > 20) {
					motionY -= 1.9f + (float) ((jumpTime - 20) / 10.0f);
				}
			} else if (UserInput.isKeyBindPressed(KeyBind.JUMP) && onGround) {
				jumpTime = 40;
			}

			if (UserInput.isKeyBindPressed(KeyBind.LEFT) && wallHit != TileFace.RIGHT) {
				motionX -= 1.2f;
			}

			if (UserInput.isKeyBindPressed(KeyBind.RIGHT) && wallHit != TileFace.LEFT) {
				motionX += 1.2f;
			}
		}
	}

	@Override
	public void renderNPC(Graphics2D g) {
		PLAYER_SPRITE.renderSpriteSheet(g, (int) (npcX), (int) (npcY - npcHeight / 2), 3.0f, 3.0f);
		g.setColor(Color.black);
		double xpos = (int) (npcX + motionX);
		double ypos = (int) (npcY + motionY);

		g.drawRect((int) (xpos - npcWidth / 2.0f), (int) (ypos - npcHeight / 2.0f), (int) (npcWidth),
				(int) (npcHeight));
	}

	@Override
	public void onCollide(Tile tile) {

	}

	@Override
	public void onCollide(NPC npc) {

	}

}
