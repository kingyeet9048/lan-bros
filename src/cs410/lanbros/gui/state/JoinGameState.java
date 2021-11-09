package cs410.lanbros.gui.state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.concurrent.TimeUnit;

import cs410.lanbros.gui.GuiButton;
import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.GuiInput;
import cs410.lanbros.main.Main;
import cs410.lanbros.networkhandler.Factory;
import cs410.lanbros.networkhandler.Client.Client;

public class JoinGameState extends GuiState {

    private Rectangle screenSize;

    public JoinGameState(GuiFrame frame, Factory factory) {
        super(frame);
        inputs = new GuiInput[] { new GuiInput("IP Address") {
        } };
        buttons = new GuiButton[] { new GuiButton("Find Host To Join") {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick(boolean pressed) {
                if (pressed) {
                    this.setEnabled(false);
                    this.setText("Please wait while we try to connect...");
                    
                    if(!Main.startClient(inputs[0].getText()))
                    {
                    	this.setText("Find Host To Join");
                        this.setEnabled(true);
                        Main.getNetworkFactory().getCurrentClient().updateHostStatus(inputs[0].getText());

                    }
                    else
                    {
                    	System.out.println(inputs[0].getText());
                    }
                }
            }
        } };
    }

    @Override
    public void renderPre(Graphics2D g) {
        g.setColor(new Color(0, 0, 20, 100));
        g.fillRect(0, 0, screenSize.width, screenSize.height);
    }

    @Override
    public void renderPost(Graphics2D g) {
        Font font = g.getFont();
        g.setColor(Color.black);
        this.drawCentered(g, font.deriveFont(50.0f), "LAN Bros!", screenSize.width / 2, 100);
        this.drawCentered(g, font.deriveFont(50.0f), "Enter an IP Address to search", screenSize.width / 2, 200);

    }

    @Override
    public void stateLoaded() {
        // TODO Auto-generated method stub
        screenSize = frame.getBounds();
        addInputWithMargin(screenSize.width / 2, 200, 15);
        addButtonsWithMargin(screenSize.width / 2, 200 + 100, 15);

    }

    @Override
    public void stateUnloaded() {
        removeButtons();
        removeInputs();
    }

}
