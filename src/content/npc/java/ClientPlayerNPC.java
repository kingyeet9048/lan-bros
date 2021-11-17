package content.npc.java;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import animation.java.SpriteSheet;
import content.tile.java.Tile;
import io.java.KeyBind;
import io.java.UserInput;

public class ClientPlayerNPC extends NPC {
	public static final SpriteSheet PLAYER_SPRITE = new SpriteSheet(new ImageIcon("resources/gfx/player_Chef1.png"));
	protected int jumpTime;
	public String playerName;

	public ClientPlayerNPC(float x, float y, String playerName) {
		super(x, y, 32, 32);
		jumpTime = 0;
		this.playerName = playerName;
	}

	@Override
	protected void updateNPC() {
		handleMovement();
	}
	
	protected void handleMovement()
	{
		if (jumpTime > 0) {
			--jumpTime;
			if (jumpTime > 20) {
				motionY -= 1.2f + (float) ((jumpTime - 20) / 10.0f);
			}
		} else if (UserInput.isKeyBindPressed(KeyBind.JUMP)) {
			jumpTime = 40;
		}

		if (UserInput.isKeyBindPressed(KeyBind.LEFT)) {
			motionX -= 1.2f;
		}

		if (UserInput.isKeyBindPressed(KeyBind.RIGHT)) {
			motionX += 1.2f;
		}
	}


	@Override
	public void renderNPC(Graphics2D g) {
		PLAYER_SPRITE.renderSpriteSheet(g, (int) (npcX - npcWidth / 2), (int) (npcY - npcHeight / 2), 3.0f, 3.0f);
	}

	@Override
	public void onCollide(Tile tile) {

	}

	@Override
	public void onCollide(NPC npc) {

	}

}
