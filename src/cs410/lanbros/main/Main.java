package cs410.lanbros.main;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cs410.lanbros.gui.GuiButton;

public class Main 
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Demo");
		frame.setBounds(100, 100, 900, 600);
		frame.setLocationRelativeTo(null); //center GUI
		JPanel panel = new JPanel();
		frame.add(panel);
		
		GuiButton button = new GuiButton("Testing!") {
			private static final long serialVersionUID = 0;
			
			public void onClick(boolean pressed)
			{
				System.out.println("Button pressed? " + pressed);
			}
		};
		
		panel.add(button);
		frame.setVisible(true);
	}
}
