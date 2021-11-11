package cs410.lanbros.content.level;

import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.content.npc.NPC;
import cs410.lanbros.content.tile.Tile;

public class Level 
{
	public ArrayList<ClientPlayerNPC> playerSet = new ArrayList<ClientPlayerNPC>();
	public Tile[][] tileMap;
	public ArrayList<NPC> npcSet = new ArrayList<NPC>();	
	private int[][] tileNeighborOffsets = {
			{-1,0}, {1,0}, {0,-1}, {0,1}
	};
	
	public Level(File levelFile)
	{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(levelFile));
			ArrayList<String> lines = new ArrayList<String>();
			String line = reader.readLine();
			int maxSize = 0;
			
			while(line != null)
			{
				lines.add(line);	
				maxSize = line.split(" ").length;
				line = reader.readLine();
			}
			
			tileMap = new Tile[lines.size()][maxSize];
			
			for(int y = 0; y < tileMap.length; ++y)
			{
				int tY = y;
				String[] tiles = lines.get(y).split(" ");
				tileMap[tY] = new Tile[tiles.length];
				
				for(int x = 0; x < tiles.length; ++x)
				{
					int tX = x;
					tileMap[tY][tX] = Tile.fromID(tiles[tX]);
					
					if(tileMap[tY][tX] != null)
						tileMap[tY][tX].setPosition(tX,tY);
				}
			}
			
			reader.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void updateLevel()
	{
		for(ClientPlayerNPC player : playerSet)
		{
			player.update();
			int tX = (int) (player.npcX/Tile.TILE_SIZE);
			int tY = (int) (player.npcY/Tile.TILE_SIZE);
			
			for(int[] off : tileNeighborOffsets)
			{
				int offX = tX + off[0], offY = tY + off[1];
				
				if(offX >= 0 && offX < tileMap.length && offY >= 0 && offY < tileMap[offX].length)
				{
					System.out.println("Checking \'"+tX+"+"+off[0]+"\', \'"+tY+"+"+off[1]);
					if(tileMap[offX][offY] != null && tileMap[offX][offY].collideWith(player))
						tileMap[offX][offY].applyCollision(player);
				}
			}
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
		
		for(Tile[] row : tileMap)
		{
			for(Tile tile : row)
			{
				if(tile != null)
					tile.renderTile(g);
			}
		}
	}
}
