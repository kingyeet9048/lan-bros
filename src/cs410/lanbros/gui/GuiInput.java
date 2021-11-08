package cs410.lanbros.gui;

import java.awt.Graphics;
import javax.swing.JTextField;

public abstract class GuiInput extends JTextField {

    private static final long serialVersionUID = 0;
    private int width, height;

    public GuiInput(String text) {
        super(text);
        setVisible(true);
        setInputSize(200, 25);
        this.setFont(getFont().deriveFont(17.0f));
    }

    public GuiInput setInputSize(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public int getInputWidth() {
        return width;
    }

    public int getInputHeight() {
        return height;
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
    }

}
