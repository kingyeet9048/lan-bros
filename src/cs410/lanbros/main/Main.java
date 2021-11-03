package cs410.lanbros.main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import cs410.lanbros.gui.GuiButton;
import cs410.lanbros.gui.SpriteSheet;
import cs410.lanbros.network.Client;
import cs410.lanbros.network.Server;
import cs410.lanbros.network.packets.InputTypes;
import cs410.lanbros.network.packets.PacketType;
import cs410.lanbros.network.packets.PlayerInputPacket;
import cs410.lanbros.security.TransitManager;

public class Main 
{
	public static final SpriteSheet test = new SpriteSheet(new ImageIcon("resources/gfx/test.png"))
			.addFrame("wink0", 2, 0, 0, 8, 8)
			.addFrame("wink1", 2, 8, 0, 8, 8)
			.addFrame("wink2", 2, 16, 0, 8, 8)
			.addAnimation("wink", 	"wink0", "wink1", "wink2", "wink1");

	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Demo");
		frame.setBounds(100, 100, 900, 600);
		frame.setLocationRelativeTo(null); //center GUI
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 0;

			public void paint(Graphics g)
			{
				super.paint(g);
				test.renderSpriteSheet((Graphics2D)g, 64, 64, 4.0f, 4.0f);
			}
		};
		
		//animate and repaint
		Timer timer = new Timer(25, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.repaint();
				test.updateSpriteSheet();
			}
		});
		timer.start();
		
		frame.add(panel);
		// test 
		//Creating a new transit manager
		TransitManager transitManger = new TransitManager("AES");
		
		// new server controlling the transit manager
		Server server = new Server(4321, "224.0.0.7", transitManger, "server 1");
		Client client = new Client(4321, "224.0.0.7", "Client 1");
		Client client2 = new Client(4321, "224.0.0.7", "Client 2");

		// joining the servetr group
		client.joinServerGroup("192.168.1.89", false);
		server.joinServerGroup("192.168.1.89", false);
		client2.joinServerGroup("192.168.1.89", false);
		// starting thread
		Thread serverThread = new Thread(server);
		Thread clientThread = new Thread(client);
		Thread clientThread2 = new Thread(client2);
		serverThread.start();
		clientThread.start();
		clientThread2.start();

		GuiButton button = new GuiButton("Testing!") {
			private static final long serialVersionUID = 0;
			
			public void onClick(boolean pressed)
			{
				System.out.println("Button pressed? " + pressed);
				if (pressed) {
					PlayerInputPacket playerInputPacket = new PlayerInputPacket("Server", client.clientName);
					playerInputPacket.setInputTypes(InputTypes.LEFT_MOVEMENT);
					playerInputPacket.setPlayerMoving(client.clientName);
					client.sendPacketToServer(playerInputPacket, PacketType.PLAYER_INPUT, true);
					
				}
				else {
					PlayerInputPacket playerInputPacket = new PlayerInputPacket("Server", client2.clientName);
					playerInputPacket.setInputTypes(InputTypes.LEFT_MOVEMENT);
					playerInputPacket.setPlayerMoving(client2.clientName);
					client2.sendPacketToServer(playerInputPacket, PacketType.PLAYER_INPUT, true);
					
				}
			}
		};
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				timer.stop();
				client.closeServerDown();
				client2.closeServerDown();
				server.closeServerDown();
				System.exit(0);				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
			}
		});
		
		panel.add(button);
		frame.setVisible(true);
	}
}

