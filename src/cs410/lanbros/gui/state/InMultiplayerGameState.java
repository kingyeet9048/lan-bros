package cs410.lanbros.gui.state;

import java.awt.Graphics2D;
import java.io.File;

import cs410.lanbros.content.level.Level;
import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.content.npc.ServerPlayerNPC;
import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.main.Main;

public class InMultiplayerGameState extends GuiState {

    public String thisPlayerName;
    int syncCounter = 100;

    public InMultiplayerGameState(GuiFrame frame, String thisPlayerName) {
        super(frame);
        this.thisPlayerName = thisPlayerName;
    }

    @Override
    public void renderPre(Graphics2D g) {
    	Level level = Main.getNetworkFactory().getCurrentClient().getCurrentLevel();
        level.updateLevel();
        level.renderLevel(g);
        
        --syncCounter;
        
        if(syncCounter <= 0)
        {
        	syncCounter = 100;
        	Main.getNetworkFactory().getCurrentClient().syncPlayerCoordinates();
        }
    }

    @Override
    public void renderPost(Graphics2D g) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stateLoaded() {
    	Level level = new Level(new File("resources/level/level0.data"));
        Main.getNetworkFactory().getCurrentClient().setCurrentLevel(level);
    }

    @Override
    public void stateUnloaded() {
        // TODO Auto-generated method stub

    }

}
