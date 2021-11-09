package cs410.lanbros.content.npc;

import java.awt.Color;
import java.awt.Graphics2D;

import cs410.lanbros.content.tile.Tile;
import cs410.lanbros.io.KeyBind;
import cs410.lanbros.io.UserInput;

public class ServerPlayerNPC extends ClientPlayerNPC {
	protected int jumpTime;
	public String playerName;

	public ServerPlayerNPC(float x, float y, String playerName) {
		super(x, y, playerName);
		jumpTime = 0;
		this.playerName = playerName;
	}

	
	protected void handleMovement()
	{
		if (jumpTime > 0) {
			--jumpTime;
			if (jumpTime > 20) {
				motionY -= 1.2f + (float) ((jumpTime - 20) / 10.0f);
			}
		} else if (UserInput.isServerKeyBindPressed(playerName, KeyBind.JUMP)) {
			jumpTime = 40;
		}

		if (UserInput.isServerKeyBindPressed(playerName, KeyBind.LEFT)) {
			motionX -= 1.2f;
		}

		if (UserInput.isServerKeyBindPressed(playerName, KeyBind.RIGHT)) {
			motionX += 1.2f;
		}		
	}

}
