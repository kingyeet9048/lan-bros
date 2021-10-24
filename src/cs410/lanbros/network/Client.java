/**
 * 
 */
package cs410.lanbros.network;

import java.util.List;

import cs410.lanbros.network.packets.Packet;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
public class Client {

	// instance variables
	Server server;
	GameGUI gameGui;
	Player player;
	Level level;
	Score score;
	List<Packet> packets;
	private String clientName;

	public Client() {
		// TODO Auto-generated constructor stub
	}
	
	public void renderClient(Graphics2D graphics) {
		// TODO Auto-generated method stub

	}
	
	public void updateClient() {
		//TODO
	}
	
	public void sendPacketToServer(Packet packet) {
		//TODO
	}
	
	public void getPacketFromServer() {
		//TODO
	}

}
