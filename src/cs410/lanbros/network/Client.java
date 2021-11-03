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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.crypto.SealedObject;
import cs410.lanbros.network.packets.ConnectionPacket;
import cs410.lanbros.network.packets.InputTypes;
import cs410.lanbros.network.packets.PacketType;
import cs410.lanbros.network.packets.PlayerInputPacket;
import cs410.lanbros.network.packets.ServerConnectedPacket;
import cs410.lanbros.network.packets.Worker;
import cs410.lanbros.network.packets.WrappedPacket;
import cs410.lanbros.security.TransitManager;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
public class Client implements Runnable, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5574801025067780379L;
	// instance variables
//	GameGUI gameGui;
//	Player player;
//	Level level;
//	Score score;
	private Queue<Serializable> packets;
	private TransitManager transitManager;
	private MulticastSocket socket;
	private DatagramSocket sendSocket;
	private NetworkInterface networkInterface;
	private SocketAddress group;
	public String clientName;
	private String machineIP;
	private List<String> clientNames;

	public Client(int port, String ipAddress, String clientName) {
		// TODO Auto-generated constructor stub
		try {
			socket = new MulticastSocket(port);
			InetAddress inet = InetAddress.getByName(ipAddress);
			group = new InetSocketAddress(inet, port);
			sendSocket = new DatagramSocket();
		} catch (IOException e) {
			System.err.printf("%s Error: %s\n", clientName, e.getMessage());
		}
		this.clientName = clientName;
		packets = new LinkedList<Serializable>();
		clientNames = new ArrayList<String>();
		transitManager = new TransitManager(Server.DEFAULTENCRYPTIONTYPE);
	}
	
	/**
	 * Need to already be connected to the LAN where the server group is. 
	 * For testing we can simply use 
	 * @param yourIP
	 */
	public void joinServerGroup(String yourIP, boolean findAutomatically) {

		try {
			if(machineIP == null) {
				machineIP = Server.getIpAddress(yourIP, findAutomatically);
			}
			InetAddress macInetAddress = InetAddress.getByName(machineIP);
			networkInterface = NetworkInterface.getByInetAddress(macInetAddress);
			System.out.println("Found network information of host machine (" + machineIP
					+ "): " + networkInterface.getName());
			socket.joinGroup(group, networkInterface);
			clientNames.add(clientName);
			ConnectionPacket newPlayer = new ConnectionPacket("Server", clientName);
			newPlayer.setPlayerName(clientName);
			sendPacketToServer(newPlayer, PacketType.PLAYER_CONNECTED, false);
			System.out.println("Joined and connected to the multicast group: "
					+ socket.isBound());
		} catch (IOException e) {
			System.err.printf("%s Error: %s\n", clientName, e.getMessage());
		}
	}
	
//	public void renderClient(Graphic2D graphics) {
//		// TODO Auto-generated method stub
//
//	}
	
	public void updateClient() {
		//TODO
	}
	
	public void sendPacketToServer(Serializable packet, PacketType packetType, boolean isEncrypted) {
		WrappedPacket wrappedPacket = new WrappedPacket(packet, packetType);
		DatagramPacket datagramPacket;
		byte[] sendingBytes;
		if (isEncrypted) {
			SealedObject object = transitManager.encryptPacket(wrappedPacket);
			System.out.println("Packaged and sealed/encrypted packet...");
			sendingBytes = Server.getByteFromObject(object);
			datagramPacket = new DatagramPacket(sendingBytes, 0, sendingBytes.length, group);
		}
		else {
			sendingBytes = Server.getByteFromObject(wrappedPacket);
			datagramPacket = new DatagramPacket(sendingBytes, 0, sendingBytes.length, group);
		}
		
		try {
			sendSocket.send(datagramPacket);
			System.out.println("Packet Send. Encrypted: " + isEncrypted);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.printf("%s Error: %s\n", clientName, e.getMessage());
		}
	}
	
	public void appendPacket(Serializable wrappedPacket) {
		packets.add(wrappedPacket);
	}
	
	public void closeServerDown() {
		try {
			socket.leaveGroup(group, networkInterface);
			sendSocket.close();
			socket.close();
			System.out.println("Closed sockets down: " + socket.isClosed());
		} catch (IOException e) {
			System.err.printf("%s Error: %s\n", clientName, e.getMessage());
		}
	}
	
	public void run() {
		Worker clientWorker = new Worker(socket, transitManager, this, false);
		Thread clientWorkerThread = new Thread(clientWorker);
		clientWorkerThread.start();
		while (!socket.isClosed()) {
			try {
				if (packets.size() >= 1) {
					Serializable object = packets.remove();
					WrappedPacket currentPacket = (WrappedPacket) object;
					switch (currentPacket.getPacketType()) {
						case PLAYER_INPUT: 
							PlayerInputPacket playerInputPacket = (PlayerInputPacket) currentPacket.getPacket();
							if ((!playerInputPacket.getPacketSender().equals(clientName)) && playerInputPacket.getPacketReceiver().equals(Server.sendToAll)) {
								// will need to make sure we are not receiving information about the same player that was moving. 
								if (clientName.equals(playerInputPacket.getPlayerMoving())) {
									break;
								}
								System.out.printf("%s says that %s is moving\n", clientName, playerInputPacket.getPlayerMoving());
							}

							break;
						case PLAYER_CONNECTED:
							ConnectionPacket newPlayer = (ConnectionPacket) currentPacket.getPacket();
							if ((!newPlayer.getPacketSender().equals(clientName)) && newPlayer.getPacketReceiver().equals(Server.sendToAll)) {
								// if this is about myself connecting, get the secret key from it. 
								if (newPlayer.getPlayerName().equals(clientName)) {
									transitManager = new TransitManager(Server.DEFAULTENCRYPTIONTYPE, newPlayer.getSecretKey());
									clientWorker.setTransitManager(transitManager);
									System.out.println("Client Says: Updated Secret Key!");
								}
								// everything else
								if (newPlayer.getPlayerName() == null) {
									break;
								}
								if (clientNames.contains(newPlayer.getPlayerName())) {
									System.out.printf("%s: Player exists. Current Players: %s\n", clientName, clientNames.toString());

									break;
								}
								else {
									clientNames.add(newPlayer.getPlayerName());
									System.out.printf("%s: Player Added. Current Players: %s\n", clientName, clientNames.toString());
								}
							}
							break;
						case SERVER_DISCONNECT:
							ConnectionPacket connectionPacket = (ConnectionPacket) currentPacket.getPacket();
							if ((!connectionPacket.getPacketSender().equals(clientName)) && connectionPacket.getPacketReceiver().equals(Server.sendToAll)) {
								System.out.println("Game has ended Something would happen here");
							}
							break;
						case SERVER_CONNECT:
							ServerConnectedPacket serverConnectedPacket =  (ServerConnectedPacket) currentPacket.getPacket();
							if ((!serverConnectedPacket.getPacketSender().equals(clientName)) && serverConnectedPacket.getPacketReceiver().equals(Server.sendToAll)) {
								ConnectionPacket identify = new ConnectionPacket("Server", clientName);
								identify.setPlayerName(clientName);
								sendPacketToServer(identify, PacketType.PLAYER_CONNECTED, false);
							}
							break;
						default:
							System.out.println("Packet Type Not Supported Yet!");
							break;
					}
				}
			} catch (NullPointerException e) {
				// TODO: handle exception
				System.err.printf("%s Error: %s\n", clientName, e.getMessage());
				ConnectionPacket newPlayer = new ConnectionPacket("Server", clientName);
				newPlayer.setPlayerName(clientName);
				sendPacketToServer(newPlayer, PacketType.PLAYER_CONNECTED, false);
				continue;
			}
		}
	}
	
	public String getName() {
		return clientName;
	}
	
	/**
	 * Test Includes:
	 * Making a new Server
	 * Making a new Client
	 * Join a server group from server
	 * Joining a server group from client
	 * Making runnables into threads
	 * Starting runnables
	 * Syncing Threads to awk server client connections
	 * Syncing Transit Manager to by exechanging secret key
	 * Adding another client to allow awk from other clients
	 * Sending Packets and allowing Workers to route and queue correctly
	 * System can read from packet queue and process it. 
	 * Next Addition: 
	 * Exchanging Server and Client Tokens instead of just the name.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
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
		clientThread.start();
		serverThread.start();
		clientThread2.start();
		
		// sending test packets to test functionlality
		Scanner scanner = new Scanner(System.in);
		String read = "";
		while(true) {
			read = scanner.nextLine();
			if (read.equals("Player 1 Move")) {
				PlayerInputPacket playerInputPacket = new PlayerInputPacket("Server", client.clientName);
				playerInputPacket.setInputTypes(InputTypes.LEFT_MOVEMENT);
				playerInputPacket.setPlayerMoving(client.clientName);
				client.sendPacketToServer(playerInputPacket, PacketType.PLAYER_INPUT, true);
				
			}
			else if (read.equals("Player 2 Move")) {
				PlayerInputPacket playerInputPacket = new PlayerInputPacket("Server", client2.clientName);
				playerInputPacket.setInputTypes(InputTypes.LEFT_MOVEMENT);
				playerInputPacket.setPlayerMoving(client2.clientName);
				client2.sendPacketToServer(playerInputPacket, PacketType.PLAYER_INPUT, true);
				
			}
//			else if (read.equals("Server Move")) {
//				PlayerInputPacket playerInputPacket = new PlayerInputPacket(Server.sendToAll, server.serverName);
//				playerInputPacket.setInputTypes(InputTypes.LEFT_MOVEMENT);
//				playerInputPacket.setPlayerMoving("Some other player");
//				server.sendPacketToClient(playerInputPacket, PacketType.PLAYER_INPUT, true);
//			}
			
			else if (read.equals("Exit")) {
				break;
			}
		}

		server.closeServerDown();
		client.closeServerDown();
		client2.closeServerDown();
		scanner.close();
	}

}
