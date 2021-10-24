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
public class Server implements Runnable {
	
	// instace variables
	private List<Client> clients;
//	private Level level;
	private List<Serializable> packets; 
	private MulticastSocket socket;
	private DatagramSocket sendSocket;
	private TransitManger transitManger;
	private SocketAddress group;
	private String ipAddress;
	private NetworkInterface networkInterface;
	private String machineIP = null;
	private int port;
	
	public Server(int port, String ipAddress, TransitManger transitManger) {
		// TODO Auto-generated constructor stub
		try {
			this.port = port;
			this.ipAddress = ipAddress;
			clients = new ArrayList<>();
			socket = new MulticastSocket(port);
			InetAddress inet = InetAddress.getByName(ipAddress);
			group = new InetSocketAddress(inet, port);
			sendSocket = new DatagramSocket();
			this.transitManger = transitManger;
		} catch (IOException e) {
			// TODO: handle exception
		}
	}
	
	private String getIpAddress() {
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
	        ex.printStackTrace();
	    }
	    return ret;
	}

	public void joinServerGroup() {
		try {
			if(machineIP == null) {
				machineIP = getIpAddress();
			}
			InetAddress macInetAddress = InetAddress.getByName(machineIP);
			networkInterface = NetworkInterface.getByInetAddress(macInetAddress);
			socket.joinGroup(group, networkInterface);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void updateInGame() {
		//TODO
	}
	
	private byte[] getByteFromObject(Serializable object) {
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
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		} finally {
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		return yourBytes;
	}
	
	private Serializable getObjectFromByte(byte[] yourBytes) {
		// https://stackoverflow.com/questions/2836646/java-serializable-object-to-byte-array
		ByteArrayInputStream bis = new ByteArrayInputStream(yourBytes);
		ObjectInput in = null;
		Serializable o = null;
		try {
		  in = new ObjectInputStream(bis);
		  o = (Serializable) in.readObject(); 
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		  try {
		    if (in != null) {
		      in.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		return o;
	}
	
	public void sendPacketToClient(Serializable packet, PacketType packetType) {
		//TODO
		WrappedPacket wrappedPacket = new WrappedPacket(packet, packetType);
		SealedObject object = transitManger.encryptPacket(wrappedPacket);
		byte[] sendingBytes = getByteFromObject(object);
		DatagramPacket datagramPacket = new DatagramPacket(sendingBytes, 0, sendingBytes.length, group);
		
		try {
			sendSocket.send(datagramPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());
		}
	}
	
	public void closeServerDown() {
		try {
			socket.leaveGroup(group, networkInterface);
			sendSocket.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] data = new byte[1000];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			socket.receive(packet);
			WrappedPacket wrappedPacket = (WrappedPacket) transitManger.decryptPacket((SealedObject) getObjectFromByte(packet.getData()));
			System.out.println(wrappedPacket.getPacketType());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// example - successful send and received of encrypted Packet through datagram
	public static void main(String args[]) {
		//Creating a new transit manager
		TransitManger transitManger = new TransitManger("AES");
		// new server controlling the transit manager
		Server server = new Server(4321, "224.2.2", transitManger);
		// joining the multicast group
		server.joinServerGroup();
		// making a new packet to send
		PlayerInputPacket inputPacket = new PlayerInputPacket();
		inputPacket.setInputTypes(InputTypes.LEFT_MOVEMENT);
		// sending the packet to all clients
		server.sendPacketToClient(inputPacket, PacketType.PLAYER_INPUT);
		// receiving the sent packet. 
		server.run();
		
	}

}
