package content.level.java;

import java.awt.Graphics2D;
import java.util.ArrayList;

import content.npc.java.ClientPlayerNPC;
import content.npc.java.NPC;
import content.tile.java.Tile;

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
