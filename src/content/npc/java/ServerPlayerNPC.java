package content.npc.java;

import content.level.java.Level;
import content.tile.java.TileFace;
import io.java.KeyBind;
import io.java.UserInput;

public class ServerPlayerNPC extends ClientPlayerNPC {
	protected int jumpTime;
	public String playerName;

	public ServerPlayerNPC(Level level, float x, float y, String playerName) {
		super(level, x, y, playerName);
		jumpTime = 0;
		this.playerName = playerName;
	}

	
	protected void handleMovement()
	{
		if (jumpTime > 0) {
			--jumpTime;
			onGround = false;
			if (jumpTime > 25) {
				motionY -= 2.2f + (float) ((jumpTime - 20) / 10.0f);
			}
		} else if (UserInput.isServerKeyBindPressed(playerName, KeyBind.JUMP) && onGround) {
			jumpTime = 40;
		}

		if (UserInput.isServerKeyBindPressed(playerName, KeyBind.LEFT) && wallHit != TileFace.RIGHT) {
			motionX -= 1.2f;
		}

		if (UserInput.isServerKeyBindPressed(playerName, KeyBind.RIGHT) && wallHit != TileFace.LEFT) {
			motionX += 1.2f;
		}		
	}

}
