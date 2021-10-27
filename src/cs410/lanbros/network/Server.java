package cs410.lanbros.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.crypto.SealedObject;

import cs410.lanbros.network.packets.ConnectionPacket;
import cs410.lanbros.network.packets.PacketType;
import cs410.lanbros.network.packets.PlayerInputPacket;
import cs410.lanbros.network.packets.Worker;
import cs410.lanbros.network.packets.WrappedPacket;
import cs410.lanbros.security.TransitManager;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
public class Server implements Runnable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2980242180582204587L;
	// instace variables
	private List<Client> clients;
//	private Level level;
	private Queue<Serializable> packets; 
	private MulticastSocket socket;
	private DatagramSocket sendSocket;
	private TransitManager transitManager;
	private SocketAddress group;
	private String ipAddress;
	private NetworkInterface networkInterface;
	private String machineIP = null;
	private int port;
	public final static String sendToAll = "ALL";
	public String serverName;
	public final static String DEFAULTENCRYPTIONTYPE = "AES";
	
	public Server(int port, String ipAddress, TransitManager transitManager, String serverName) {
		// TODO Auto-generated constructor stub
		try {
			this.port = port;
			this.ipAddress = ipAddress;
			clients = new ArrayList<>();
			socket = new MulticastSocket(port);
			InetAddress inet = InetAddress.getByName(ipAddress);
			group = new InetSocketAddress(inet, port);
			sendSocket = new DatagramSocket();
			this.transitManager = transitManager;
			this.serverName = serverName;
			packets = new LinkedList<Serializable>();
		} catch (IOException e) {
			// TODO: handle exception
			System.err.println("Server Error: " + e.getMessage());
		}
	}
	
	public static String getIpAddress() {
		// https://www.programcreek.com/java-api-examples/?api=java.net.NetworkInterface
		String ret = "";
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
	                if (!inetAddress.isLoopbackAddress()) {
	                    ret = inetAddress.getHostAddress().toString();
	                }
	            }
	        }
	    } catch (SocketException ex) {
			System.err.println("Server Error: " + ex.getMessage());
	    }
	    return ret;
	}

	public void joinServerGroup() {
		try {
			if(machineIP == null) {
				System.out.println("Getting IP Address of the current machine...");
				machineIP = getIpAddress();
				System.out.println("Obtained IP Adress: " + machineIP);
			}
			System.out.println("Getting Network of host IP Address...");
			InetAddress macInetAddress = InetAddress.getByName(machineIP);
			networkInterface = NetworkInterface.getByInetAddress(macInetAddress);
			System.out.println("Found network information of host machine (" + machineIP
					+ "): " + networkInterface.getName());
			socket.joinGroup(group, networkInterface);
			System.out.println("Joined and connected to the multicast group (" + ipAddress + "): "
					+ socket.isBound());
		} catch (IOException e) {
			System.err.println("Server Error: " + e.getMessage());
		}
	}
	
	public void updateInGame() {
		//TODO
	}
	
	public static byte[] getByteFromObject(Serializable object) {
		// https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		byte[] yourBytes = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(object);
		  out.flush();
		  yourBytes = bos.toByteArray();
		} catch (IOException e) {
			System.err.println("Server Error: " + e.getMessage());
		} finally {
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
				System.err.println("Server Error: " + ex.getMessage());
		  }
		}
		return yourBytes;
	}
	
	public static Serializable getObjectFromByte(byte[] yourBytes) {
		// https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
		ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
		ObjectInput in = null;
		Serializable o = null;
		try {
		  in = new ObjectInputStream(bis);
		  o = (Serializable) in.readObject(); 
		} catch (IOException | ClassNotFoundException e) {
			System.err.println("Server Error: " + e.getMessage());
		} finally {
		  try {
		    if (in != null) {
		      in.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
				System.err.println("Server Error: " + ex.getMessage());
		  }
		}
		return o;
	}
	
	public void sendPacketToClient(Serializable packet, PacketType packetType, boolean isEncrypted) {
		//TODO
		System.out.println("Sending Packet type (" + packetType + ") to receivers...");
		WrappedPacket wrappedPacket = new WrappedPacket(packet, packetType);
		DatagramPacket datagramPacket;
		byte[] sendingBytes;
		if (isEncrypted) {
			SealedObject object = transitManager.encryptPacket(wrappedPacket);
			System.out.println("Packaged and sealed/encrypted packet...");
			sendingBytes = getByteFromObject(object);
			datagramPacket = new DatagramPacket(sendingBytes, 0, sendingBytes.length, group);
		}
		else {
			System.out.println("Packet is not going to be encrypted. Sending raw wrapped.");
			sendingBytes = getByteFromObject(wrappedPacket);
			datagramPacket = new DatagramPacket(sendingBytes, 0, sendingBytes.length, group);
		}
		
		try {
			sendSocket.send(datagramPacket);
			System.out.println("Broadcast sucessfull!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Server Error: " + e.getMessage());
		}
	}
	
	public void closeServerDown() {
		try {
			socket.leaveGroup(group, networkInterface);
			sendSocket.close();
			socket.close();
			System.out.println("Closed sockets down: " + socket.isClosed());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Server Error: " + e.getMessage());
		}
	}
	
	public SocketAddress getGroup() {
		return group;
	}
	
	public String getName() {
		return serverName;
	}
	
	public void appendPacket(Serializable packet) {
		packets.add(packet);
	}

	@Override
	public void run() {
		Worker serverWorker = new Worker(socket, transitManager, this, true);
		Thread serverWorkerThread = new Thread(serverWorker);
		serverWorkerThread.start();
		while (!socket.isClosed()) {
			try {
				if (packets.size() >= 1) {
					Serializable object = packets.remove();
					WrappedPacket currentPacket = (WrappedPacket) object;
					switch (currentPacket.getPacketType()) {
						case PLAYER_INPUT: 
							PlayerInputPacket playerInputPacket = (PlayerInputPacket) currentPacket.getPacket();
							if ((!playerInputPacket.getPacketSender().equals(serverName)) && playerInputPacket.getPacketReceiver().equals("Server")) {
								System.out.println("Server says: Client moving: " + playerInputPacket.getInputTypes());
								playerInputPacket.setPacketSender(serverName);
								playerInputPacket.setPacketReceiver(sendToAll);
								sendPacketToClient(currentPacket.getPacket(), PacketType.PLAYER_INPUT, true);
							}
							break;
						case PLAYER_CONNECTED:
							ConnectionPacket newPlayer = (ConnectionPacket) currentPacket.getPacket();
							if ((!newPlayer.getPacketSender().equals(serverName)) && newPlayer.getPacketReceiver().equals("Server")) {
								System.out.println("Server says: Client connecting: " + newPlayer.getPlayerName());
								newPlayer.setPacketReceiver(sendToAll);
								newPlayer.setPacketSender(serverName);
								newPlayer.setSecretKey(transitManager.getSecretKey());
								sendPacketToClient(currentPacket.getPacket(), PacketType.PLAYER_CONNECTED, false);
							}
							break;
						case SERVER_DISCONNECT:
							ConnectionPacket connectionPacket = (ConnectionPacket) currentPacket.getPacket();
							if ((!connectionPacket.getPacketSender().equals(serverName)) && connectionPacket.getPacketReceiver().equals("Server")) {
								System.out.println("Game has ended Something would happen here");
							}
							break;
						default:
							System.out.println("Unrecognized Packet Type!");
							break;
					}
				}
			} catch (NullPointerException e) {
				System.err.printf("%s Error: %s\n", serverName, e.getMessage());
				ConnectionPacket newPlayer = new ConnectionPacket(sendToAll, serverName);
				sendPacketToClient(newPlayer, PacketType.PLAYER_CONNECTED, false);
				continue;
			}
		}
	}

}
