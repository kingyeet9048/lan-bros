package cs410.lanbros.main;

import javax.swing.JFrame;
import javax.swing.JPanel;

import cs410.lanbros.gui.GuiButton;
import cs410.lanbros.network.Client;
import cs410.lanbros.network.Server;
import cs410.lanbros.network.packets.InputTypes;
import cs410.lanbros.network.packets.PacketType;
import cs410.lanbros.network.packets.PlayerInputPacket;
import cs410.lanbros.security.TransitManager;

public class Main 
{
	public static void main(String[] args)
	{
		JFrame frame = new JFrame("Demo");
		frame.setBounds(100, 100, 900, 600);
		frame.setLocationRelativeTo(null); //center GUI
		JPanel panel = new JPanel();
		frame.add(panel);
		// test 
		//Creating a new transit manager
		TransitManager transitManger = new TransitManager("AES");
		
		// new server controlling the transit manager
		Server server = new Server(4321, "224.0.0.7", transitManger, "server 1");
		Client client = new Client(4321, "224.0.0.7", "Client 1");
		Client client2 = new Client(4321, "224.0.0.7", "Client 2");

		// joining the servetr group
		client.joinServerGroup("192.168.1.89", true);
		server.joinServerGroup("192.168.1.89", true);
		client2.joinServerGroup("192.168.1.89", true);
		// starting thread
		Thread serverThread = new Thread(server);
		Thread clientThread = new Thread(client);
		Thread clientThread2 = new Thread(client2);
		clientThread.start();
		serverThread.start();
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
		
		panel.add(button);
		frame.setVisible(true);
	}
}
