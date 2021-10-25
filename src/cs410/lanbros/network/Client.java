/**
 * 
 */
package cs410.lanbros.network;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.List;

import javax.crypto.SealedObject;

import cs410.lanbros.network.packets.InputTypes;
import cs410.lanbros.network.packets.PacketType;
import cs410.lanbros.network.packets.PlayerInputPacket;
import cs410.lanbros.network.packets.WrappedPacket;
import cs410.lanbros.security.TransitManger;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
public class Client {

	// instance variables
//	GameGUI gameGui;
//	Player player;
//	Level level;
//	Score score;
	List<Serializable> packets;
	private TransitManger transitManger;
	private MulticastSocket socket;
	private DatagramSocket sendSocket;
	private NetworkInterface networkInterface;
	private SocketAddress group;
	public String clientName;
	private String machineIP;
	
	public Client(int port, String ipAddress, TransitManger transitManger, String clientName) {
		// TODO Auto-generated constructor stub
		try {
			socket = new MulticastSocket(port);
			InetAddress inet = InetAddress.getByName(ipAddress);
			group = new InetSocketAddress(inet, port);
			sendSocket = new DatagramSocket();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Client Error: " + e.getMessage());
		}
		this.clientName = clientName;
		this.transitManger = transitManger;
	}
	
	public void joinServerGroup() {

		try {
			if(machineIP == null) {
				System.out.println("Getting IP Address of the current machine...");
				machineIP = Server.getIpAddress();
				System.out.println("Obtained IP Adress: " + machineIP);
			}
			System.out.println("Getting Network of host IP Address...");
			InetAddress macInetAddress = InetAddress.getByName(machineIP);
			networkInterface = NetworkInterface.getByInetAddress(macInetAddress);
			System.out.println("Found network information of host machine (" + machineIP
					+ "): " + networkInterface.getName());
			socket.joinGroup(group, networkInterface);
			System.out.println("Joined and connected to the multicast group: "
					+ socket.isBound());
		} catch (IOException e) {
			System.err.println("Client Error: " + e.getMessage());
		}
	}
	
//	public void renderClient(Graphic2D graphics) {
//		// TODO Auto-generated method stub
//
//	}
	
	public void updateClient() {
		//TODO
	}
	
	public void sendPacketToServer(Serializable packet, PacketType packetType) {
		//TODO
		System.out.println("Sending Packet type (" + packetType + ") to receivers...");
		WrappedPacket wrappedPacket = new WrappedPacket(packet, packetType);
		SealedObject object = transitManger.encryptPacket(wrappedPacket);
		System.out.println("Packaged and sealed/encrypted packet...");
		byte[] sendingBytes = Server.getByteFromObject(object);
		DatagramPacket datagramPacket = new DatagramPacket(sendingBytes, 0, sendingBytes.length, group);
		
		try {
			sendSocket.send(datagramPacket);
			System.out.println("Broadcast sucessfull!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Client Error: " + e.getMessage());
		}
	}
	
	public void getPacketFromServer() {
		//TODO
		byte[] data = new byte[1000];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			// exmple of use. Will change
			socket.receive(packet);
			while (packet.getLength() != 0) {
				WrappedPacket wrappedPacket = (WrappedPacket) transitManger.decryptPacket((SealedObject) Server.getObjectFromByte(packet.getData()));
				if ((!((PlayerInputPacket) wrappedPacket.getPacket()).getPacketSender().equals(clientName)) && ((PlayerInputPacket) wrappedPacket.getPacket()).getPacketReceiver().equals(Server.sendToAll) ) {
					System.out.println("CURRENT Client: " + clientName + " Type of Packet: " + wrappedPacket.getPacketType() + " Receiver: " + ((PlayerInputPacket) wrappedPacket.getPacket()).getPacketReceiver() + " Sender: " + (((PlayerInputPacket) wrappedPacket.getPacket()).getPacketSender()));
				}
				else {
					System.out.println("Packet not indended for this system");
				}
				socket.receive(packet);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Client Error: " + e.getMessage());
		}
	}
	
	public static void main(String args[]) {
		//Creating a new transit manager
		TransitManger transitManger = new TransitManger("AES");
		// new server controlling the transit manager
		Server server = new Server(4321, "224.2.2", transitManger, "server 1");
		Client client = new Client(4321, "224.2.2", transitManger, "Client 1");
		server.joinServerGroup();
		client.joinServerGroup();
		
		// sending input packet to server
		PlayerInputPacket inputPacket = new PlayerInputPacket("Server", client.clientName);
		inputPacket.setInputTypes(InputTypes.UP_MOVEMENT);
		client.sendPacketToServer(inputPacket, PacketType.PLAYER_INPUT);
		server.run();
		
		PlayerInputPacket secondInput = new PlayerInputPacket(Server.sendToAll, "Server");
		inputPacket.setInputTypes(InputTypes.UP_MOVEMENT);
		server.sendPacketToClient(secondInput, PacketType.PLAYER_INPUT);
		client.getPacketFromServer();
		
		
//		server.closeServerDown();
	}

}
