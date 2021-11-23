package content.npc.java;

import java.awt.Graphics2D;

import javax.swing.ImageIcon;

import animation.java.SpriteSheet;
import content.level.java.Level;
import content.tile.java.NPCTile;
import content.tile.java.Tile;
import content.tile.java.TileFace;

public class SpikeBallNPC extends NPC 
{
	public static final SpriteSheet SPIKE_SPRITE = new SpriteSheet(new ImageIcon("resources/gfx/spikeball.png"))
			.addFrame("2", 10000, 0, 24, 24, 24)
			.addFrame("1", 10000, 0, 0, 24, 24)
			.addAnimation("1", "1")
			.addAnimation("2", "2");
	private static int MAX_ATTACK_TIME = 40;
	private int attackTime; 
	
	public SpikeBallNPC(Level level, float x, float y) {
		super(level, x, y, 32, 32);
		attackTime = MAX_ATTACK_TIME;
		setMaxLife(2);
	}

	@Override
	protected void updateNPC() 
	{
		if(attackTime == 0 && this.onGround)
		{
			attackTime = MAX_ATTACK_TIME;
			motionX = (float) (Math.random() * 5.0f - 2.5f);
		}
		else
		{
			onGround = false;
			if (attackTime > 20) {
				motionY -= 1.9f + (float) ((attackTime - 20) / 10.0f);
			}
			
			--attackTime;
			
			if(attackTime == 0)
			{
				attackTime = -1;
			}
		}
	}

	@Override
	public void renderNPC(Graphics2D g) 
	{
		SPIKE_SPRITE.setCurrentFrame(life+"");
		SPIKE_SPRITE.renderSpriteSheet(g, (int) (npcX), (int) (npcY - npcHeight / 2-6.0f), 3.0f, 3.0f, (float) Math.toRadians(this.lifeTime * 6), 0, 0);
	}

	@Override
	public void onCollide(Tile tile, TileFace face) 
	{
		if(attackTime <= -1)
		{
			attackTime = 0;
		}
	}

	@Override
	public void onCollide(NPC npc) 
	{
		if(npc.npcY - npcY > 6 && npc.dmgCooldown <= 0)
		{
			damageNPC(1);
			motionX = (npcX - npc.npcX);
			motionY = -Math.abs(npcY - npc.npcY) / 2.0f - 1.0f;			
		}
		else if(dmgCooldown <= 0)
		{
			npc.damageNPC(1);
			npc.motionX = (npc.npcX - npcX) / 4.0f;
			npc.motionY = -Math.abs(npc.npcY - npcY) / 2.0f - 1.0f;			
		}
	}

	@Override
	public Tile createTile(Level level) {
		return new NPCTile(level, 0, 0, new SpikeBallNPC(level,0,0));
	}

	@Override
	public String getTileID() 
	{
		return "e";
	}

	@Override
	public boolean shouldCollideFromSide(TileFace direction) //itileentry
	{
		return false;
	}

	@Override
	public boolean collideWith(NPC npc) //itileentry
	{
		return false;
	}

	@Override
	public void applyCollision(NPC npc) {} //itileentry

	@Override
	protected void onNPCKill() {
		
	}
	
}
