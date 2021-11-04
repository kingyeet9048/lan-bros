package cs410.lanbros.main;

import javax.swing.ImageIcon;

import cs410.lanbros.animation.SpriteSheet;
import cs410.lanbros.gui.GuiButton;
import cs410.lanbros.gui.GuiFrame;
import cs410.lanbros.gui.state.TestState;
import cs410.lanbros.network.Client;
import cs410.lanbros.network.Server;
import cs410.lanbros.network.packets.InputTypes;
import cs410.lanbros.network.packets.PacketType;
import cs410.lanbros.network.packets.PlayerInputPacket;
import cs410.lanbros.security.TransitManager;

public class Main 
{
	public static final SpriteSheet test = new SpriteSheet(new ImageIcon("resources/gfx/test.png"))
			.addFrame("wink0", 15, 0, 0, 8, 8)
			.addFrame("wink1", 10, 8, 0, 8, 8)
			.addFrame("wink2", 15, 16, 0, 8, 8)
			.addAnimation("wink", 	"wink0", "wink1", "wink2", "wink1");

	public static void main(String[] args)
	{
		GuiFrame frame = new GuiFrame();
		frame.addActiveState(new TestState());

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
		button.setBounds(160, 66, 128, 32);
		frame.getActivePanel().add(button);

		
		new Thread(()-> {
			while(!frame.isClosed())
			{
				try {
					Thread.sleep(1000);					
				}
				catch(InterruptedException e)
				{
					break;
				}
			}
			
			client.closeServerDown();
			client2.closeServerDown();
			server.closeServerDown();
			System.exit(0);
		}).start();
		
		
	}
}

