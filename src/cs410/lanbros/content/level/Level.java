package cs410.lanbros.content.level;

import java.awt.Graphics2D;
import java.util.ArrayList;

import cs410.lanbros.content.npc.NPC;
import cs410.lanbros.content.npc.PlayerNPC;
import cs410.lanbros.content.tile.Tile;

public class Level 
{
	public ArrayList<PlayerNPC> playerSet = new ArrayList<PlayerNPC>();
	public ArrayList<Tile> tileSet = new ArrayList<Tile>();
	public ArrayList<NPC> npcSet = new ArrayList<NPC>();	
	
	public void updateLevel()
	{
		for(PlayerNPC player : playerSet)
		{
			player.update();
		}
	}
	
	public void resetLevel()
	{
		
	}
	
	public void renderLevel(Graphics2D g)
	{
		for(PlayerNPC player : playerSet)
		{
			player.renderNPC(g);
		}
	}
}
