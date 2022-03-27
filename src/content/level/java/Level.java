package content.level.java;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import content.npc.java.ClientPlayerNPC;
import content.npc.java.NPC;
import content.tile.java.ITileEntry;
import content.tile.java.NPCTile;
import content.tile.java.Tile;
import content.tile.java.TileFace;

public class Level 
{
	public ArrayList<ClientPlayerNPC> playerSet = new ArrayList<ClientPlayerNPC>();
	public ArrayList<NPC> npcSet = new ArrayList<NPC>();
	private ArrayList<NPC> removalQueue = new ArrayList<NPC>();
	private Tile[][] tileMap;
	private int[] heightMap;
	private final File levelFile;
	
	public Level(File file)
	{
		levelFile = file;
		if(!loadFromFile(file))
		{
			System.err.println("** WARNING!!! Unable to load level from file \'"+file.toString()+"\'!!! **");
		}
	}
	
	protected boolean loadFromFile(File file)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(levelFile));
			ArrayList<String> lines = new ArrayList<String>();
			String line = reader.readLine();
			int maxSize = 0;
			
			while(line != null)
			{
				lines.add(line);	
				line = reader.readLine();
			}
			
			maxSize = lines.get(0).split(" ").length;
			tileMap = new Tile[lines.size()][maxSize];
			heightMap = new int[lines.size()];
			
			for(int x = 0; x < tileMap.length; ++x)
			{
				String[] tiles = lines.get(x).split(" ");
				tileMap[x] = new Tile[tiles.length];
				heightMap[x] = tiles.length;
				
				for(int y = 0; y < tiles.length; ++y)
				{
					Tile tile = Tile.fromID(this, tiles[y]);
					
					if(tile == null)
					{
						tile = NPC.fromID(this, tiles[y]);
					}
					
					tileMap[x][y] = tile;
					
					if(tile != null)
					{
						tile.setPosition(x,y);
						
						if(tile instanceof NPCTile)
						{
							tile.updateTile();
						}
						
						if(((ITileEntry)tile).shouldCollideFromSide(TileFace.TOP))
						{
							heightMap[x] = heightMap[x] > y ? y : heightMap[x]; 
						}
					}
				}
			}
			
			reader.close();
			return true;
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return false;
	}
	
	public synchronized void updateLevel()
	{
		synchronized(playerSet)
		{
			if(playerSet.size() > 0)
			{
				for(ClientPlayerNPC player : playerSet)
				{
					if(player != null)
					{
						player.update();					
					}
				}			
			}			
		}
		
		synchronized(npcSet)
		{
			if(npcSet.size() > 0)
			{
				for(NPC npc : npcSet)
				{
					npc.update();
					
					if (playerSet.size() > 0)
					{
						for(ClientPlayerNPC player : playerSet)
						{
							if(player != null)
							{
								float xDist = Math.abs(player.npcX-npc.npcX), yDist = Math.abs(player.npcY - npc.npcY);
								
								if(xDist < npc.npcWidth && yDist < npc.npcHeight) {
									npc.onCollide(player);
									player.onCollide(npc);
								}
							}
						}				
					}					
				}			
			}			
		}
		
		for(Tile[] tilerow : tileMap)
		{
			for(Tile tile : tilerow)
			{
				if(tile != null)
				{
					tile.updateTile();					
				}
			}
		}
		
		while(removalQueue.size() > 0)
		{
			NPC npc = removalQueue.remove(0);
			
			if(npc instanceof ClientPlayerNPC)
			{
				playerSet.remove(npc);
			}
			else 
			{
				npcSet.remove(npc);
			}
		}
	}
	
	public void queueForRemoval(NPC npc)
	{
		removalQueue.add(npc);
	}
	
	public void resetLevel()
	{
		loadFromFile(levelFile);
	}
	
	public synchronized void renderLevel(Graphics2D g)
	{
		for(ClientPlayerNPC player : playerSet)
		{
			if(player != null)
			{
				player.renderNPC(g);				
			}
		}
		
		for(NPC npc : npcSet)
		{
			if(npc != null)
			{
				npc.renderNPC(g);				
			}
		}
		
		for(Tile[] row : tileMap)
		{
			for(Tile tile : row)
			{
				if(tile != null)
					tile.renderTile(g);
			}
		}
		
		for(Tile[] row : tileMap)
		{
			for(Tile tile : row)
			{
				if(tile != null)
					tile.renderTile(g);
			}
		}
	}

	public Tile[][] getTileMap() {
		return tileMap;
	}
	
	public int[] getHeightMap() {
		return heightMap;
	}
}
