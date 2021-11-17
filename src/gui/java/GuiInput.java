package gui.java;

import java.awt.Graphics;
import javax.swing.JTextField;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public abstract class GuiInput extends JTextField implements FocusListener {

    private static final long serialVersionUID = 0;
    private int width, height;
    private String defaultText;

    public GuiInput(String text) {
        super(text);
        defaultText = text;
        this.setVisible(true);
        this.setInputSize(200, 25);
        this.setFont(getFont().deriveFont(17.0f));
        this.addFocusListener(this);
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

    @Override
    public void focusGained(FocusEvent e) {
        if (e.getSource() == this) {
            if (getText().equals(defaultText)) {
                setText("");
            }
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
    	if(e.getSource() == this && getText().isEmpty())
    	{
    		setText(defaultText);
    	}
    }
}
