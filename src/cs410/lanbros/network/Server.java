package cs410.lanbros.network;

import java.util.List;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
public class Server implements Runnable {
	
	// instace variables
	List<Client> clients;
	Level level;
	List<Packet> packets; 
	
	public Server() {
		// TODO Auto-generated constructor stub
	}
	
	public void updateInGame() {
		//TODO
	}
	
	public void sendPacketToClient(Packet packet) {
		//TODO
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
