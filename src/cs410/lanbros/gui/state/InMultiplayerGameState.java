package cs410.lanbros.gui.state;

import java.awt.Graphics2D;

import cs410.lanbros.content.level.Level;
import cs410.lanbros.content.npc.ClientPlayerNPC;
import cs410.lanbros.content.npc.ServerPlayerNPC;
import cs410.lanbros.gui.GuiFrame;

public class InMultiplayerGameState extends GuiState {

    public Level currentLevel;
    public String thisPlayerName;

    public InMultiplayerGameState(GuiFrame frame, String thisPlayerName) {
        super(frame);
        this.thisPlayerName = thisPlayerName;
    }

    @Override
    public void renderPre(Graphics2D g) {
        currentLevel.updateLevel();
        currentLevel.renderLevel(g);
    }

    @Override
    public void renderPost(Graphics2D g) {
        // TODO Auto-generated method stub

    }

    @Override
    public void stateLoaded() {
        currentLevel = new Level();
        currentLevel.playerSet.add(new ClientPlayerNPC(3, 3, thisPlayerName));
    }

    public void addNewPlayer(String playerName) {
        boolean playerLoaded = false;
        for (ClientPlayerNPC player : currentLevel.playerSet) {
            if (player.playerName.equals(playerName)) {
                playerLoaded = true;
                break;
            }
        }
        if (!playerLoaded) {
            currentLevel.playerSet.add(new ServerPlayerNPC(3, 3, playerName));
        }
    }

    @Override
    public void stateUnloaded() {
        // TODO Auto-generated method stub

    }

}
