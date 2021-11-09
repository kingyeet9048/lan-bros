package cs410.lanbros.gui.state;

import java.awt.Graphics2D;

import cs410.lanbros.content.level.Level;
import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.content.npc.ServerPlayerNPC;
import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.main.Main;

public class InMultiplayerGameState extends GuiState {

    public String thisPlayerName;

    public InMultiplayerGameState(GuiFrame frame, String thisPlayerName) {
        super(frame);
        this.thisPlayerName = thisPlayerName;
    }

    @Override
    public void renderPre(Graphics2D g) {
    	Level level = Main.getNetworkFactory().getCurrentClient().getCurrentLevel();
        level.updateLevel();
        level.renderLevel(g);
    }

    @Override
    public void renderPost(Graphics2D g) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stateLoaded() {
    	Level level = new Level();
        Main.getNetworkFactory().getCurrentClient().setCurrentLevel(level);
        level.playerSet.add(new ClientPlayerNPC(3, 3, thisPlayerName));
    }

    @Override
    public void stateUnloaded() {
        // TODO Auto-generated method stub

    }

}
