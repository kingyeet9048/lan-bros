package cs410.lanbros.content.level;

import java.awt.Graphics2D;
import java.util.ArrayList;

import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.content.npc.NPC;
import cs410.lanbros.content.tile.Tile;

public class Level 
{
	public ArrayList<ClientPlayerNPC> playerSet = new ArrayList<ClientPlayerNPC>();
	public ArrayList<Tile> tileSet = new ArrayList<Tile>();
	public ArrayList<NPC> npcSet = new ArrayList<NPC>();	
	
	public synchronized void updateLevel()
	{
		for(ClientPlayerNPC player : playerSet)
		{
			player.update();
		}
	}
	
	public void resetLevel()
	{
		
	}
	
	public synchronized void renderLevel(Graphics2D g)
	{
		for(ClientPlayerNPC player : playerSet)
		{
			player.renderNPC(g);
		}
	}
}
