package content.npc.java;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

import animation.java.SpriteSheet;
import content.level.java.Level;
import content.tile.java.NPCTile;
import content.tile.java.Tile;
import content.tile.java.TileFace;
import io.java.KeyBind;
import io.java.UserInput;
import main.java.Main;
import networkhandler.client.java.Client;

public class ClientPlayerNPC extends NPC {
	public static final SpriteSheet PLAYER_SPRITE = new SpriteSheet(new ImageIcon("resources/gfx/player_Chef1.png"));
	protected int jumpTime;
	public String playerName;
	
	public ClientPlayerNPC(Level level, float x, float y, String playerName) {
		super(level, x, y, 32, 32);
		jumpTime = 0;
		this.playerName = playerName;
		
		if(Main.getNetworkFactory().getCurrentClient() != null)
		{
			Client client = Main.getNetworkFactory().getCurrentClient();
			
			if(client.getThisPlayer() == null)
			{
				client.setThisPlayer(this);
			}
		}
	}

	@Override
	protected void updateNPC() {
		handleMovement();
	}
	
	protected void handleMovement()
	{
		if(Main.getNetworkFactory().getCurrentClient() == null || Main.getNetworkFactory().getCurrentClient().canClientMove())
		{
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
		PLAYER_SPRITE.renderSpriteSheet(g, (int) (npcX), (int) (npcY - npcHeight / 2-6.0f), 3.0f, 3.0f);
		double xpos = (int)(npcX);
		double ypos = (int)(npcY);
		g.setColor(Color.black);
		g.drawString(life+"/"+lifeMax,(int)(xpos-npcWidth/2.0f),(int)(ypos-npcHeight-32));
		
		if(Main.getNetworkFactory().getCurrentClient() != null)
			g.drawString(playerName,(int)(xpos-npcWidth/2.0f),(int)(ypos-npcHeight-44));
//		g.setColor(Color.black);
//		g.drawRect((int)(xpos-npcWidth/2.0f),(int)(ypos-npcHeight/2.0f),(int)(npcWidth),(int)(npcHeight));
	}

	@Override
	public void onCollide(Tile tile, TileFace face) {

	}

	@Override
	public void onCollide(NPC npc) {

	}

	@Override
	public Tile createTile(Level level) {
		return new NPCTile(level, 0, 0, new ClientPlayerNPC(level,0,0,Main.getNetworkFactory().getCurrentClient() != null ? Main.getNetworkFactory().getCurrentClient().getThisPlayerName() : "null"));
	}

	@Override
	public String getTileID() {
		// TODO Auto-generated method stub
		return "P";
	}

	@Override
	public boolean shouldCollideFromSide(TileFace direction) {
		return false;
	}

	@Override
	public boolean collideWith(NPC npc) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void applyCollision(NPC npc) {
	}

	@Override
	protected void onNPCKill() {
		
	}

}
