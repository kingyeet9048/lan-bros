package cs410.lanbros.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cs410.lanbros.network.packets.Packet;
import cs410.lanbros.security.TransitManger;

/**
 * @CreatedBy Sulaiman Bada
 *
 */
public class Server implements Runnable {
	
	// instace variables
	private List<Client> clients;
	private Level level;
	private List<Packet> packets; 
	private MulticastSocket socket;
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
	
	public void sendPacketToClient(Packet packet) {
		//TODO
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	// example
	public static void main(String args[]) {
		Server server = new Server(4321, "224.2.2", null);
		server.joinServerGroup();
		System.out.println(server);
	}

}
