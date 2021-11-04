package cs410.lanbros.gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import cs410.lanbros.gui.state.GuiState;

/**
 * 
 * A JFrame child that handles a layered system of states, as well as periodically repaints itself to update animations present.
 * 
 * @author Ashton Schultz
 *
 */
public class GuiFrame extends JFrame
{
	private static final long serialVersionUID = 0;

	/**
	 * A constant telling the time, in milliseconds, between each SpriteSheet frame update.
	 */
	public static final int ANIMATION_INTERVAL = 1000/60;
	
	//repaint for animation
	protected final Timer animationTimer = new Timer(ANIMATION_INTERVAL, new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			activePanel.repaint();
		}
	});
	
	protected final JPanel activePanel = new JPanel() {
		private static final long serialVersionUID = 0;

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			renderStates((Graphics2D)g);
		}
	};;
	
	//List of active states in this GuiFrame.
	protected final LinkedList<GuiState> activeStates = new LinkedList<GuiState>();
	//state flag for the frame
	private boolean frameClosed = false;
	
	public GuiFrame()
	{
		super("Demo");
		setBounds(100, 100, 900, 600);
		setLocationRelativeTo(null); //center GUI

		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosing(WindowEvent e) {
				animationTimer.stop();
				frameClosed = true;
				activeStates.clear();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		add(activePanel);
		animationTimer.start();
		setVisible(true);
	}
	
	/**
	 * Updates all active GuiStates present in this GuiFrame.
	 * @param g the graphics object to render the states to.
	 */
	public void renderStates(Graphics2D g)
	{		
		for(GuiState state : activeStates)
			state.render(g);
	}
	
	/**
	 * Attempts to add an active GuiState to this GuiFrame.
	 * @param state the state to add.
	 * @return true if the state was not already active, false if it was.
	 */
	public boolean addActiveState(GuiState state)
	{
		if(activeStates.contains(state))
		{
			return false;
		}
		else
		{
			return activeStates.add(state);
		}
	}
	
	/**
	 * Attempts to remove an active GuiState from this GuiFrame.
	 * @param state the state to remove.
	 * @return true if the state was active and removed, false if the state was not active.
	 */
	public boolean removeActiveState(GuiState state)
	{
		if(activeStates.contains(state))
		{
			return activeStates.remove(state);
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Empties all currently active states in this GuiFrame.
	 */
	public void wipeActiveStates()
	{
		activeStates.clear();
	}
	
	/**
	 * @return the active panel in this GuiFrame.
	 */
	public JPanel getActivePanel()
	{
		return activePanel;
	}
	
	/**
	 * @return whether this GuiFrame was closed or not.
	 */
	public boolean isClosed()
	{
		return frameClosed;
	}
}
