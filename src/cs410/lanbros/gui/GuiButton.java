package cs410.lanbros.gui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

/**
 * 
 * @author Ashton Schultz
 */
public abstract class GuiButton extends JButton implements MouseListener
{
	private static final long serialVersionUID = 0;
	public GuiButton(String text)
	{
		super(text);
		setVisible(true);
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics graphics)
	{
		super.paintComponent(graphics);
		//Graphics2D g = (Graphics2D)graphics; //Prep for when images come in for buttons, if necessary
	}
	
	/**
	 * Called from a {@link MouseListener} when the left mouse button is pressed or released on this button.
	 * @param pressed true if the button was pressed, false if the button was released
	 */
	public abstract void onClick(boolean pressed);
	
	/**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     */
    public void mouseClicked(MouseEvent e) {}

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(MouseEvent e) {
    	if(e.getButton() == MouseEvent.BUTTON1 && isEnabled() && isVisible()) //left click
    	{
    		onClick(true);
    	}
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(MouseEvent e) {
    	if(e.getButton() == MouseEvent.BUTTON1 && isEnabled() && isVisible()) //left click
    	{
    		onClick(false);
    	}
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(MouseEvent e) {}
}
