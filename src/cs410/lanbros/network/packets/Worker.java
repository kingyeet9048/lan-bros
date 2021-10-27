package cs410.lanbros.network.packets;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.concurrent.TimeUnit;

import javax.crypto.SealedObject;

import cs410.lanbros.network.Client;
import cs410.lanbros.network.Server;
import cs410.lanbros.security.TransitManager;

public class Worker implements Runnable{

	private MulticastSocket socket;
	private TransitManager transitManager;
	private Serializable master;
	private boolean isServer;

	public Worker(MulticastSocket socket, TransitManager transitManager, Serializable master, boolean isServer) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.transitManager = transitManager;
		this.master = master;
		this.isServer = isServer;
	}

	public void setTransitManager(TransitManager transitManager) {
		this.transitManager = transitManager;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		byte[] data = new byte[1000];
		DatagramPacket packet = new DatagramPacket(data, data.length);
			// exmple of use. Will change
		while (!socket.isClosed()) {
			try {
				TimeUnit.MILLISECONDS.sleep(10);
				socket.setSoTimeout(100);
				socket.receive(packet);
				WrappedPacket wrappedPacket = (WrappedPacket) transitManager.decryptPacket((SealedObject) Server.getObjectFromByte(packet.getData()));
				if (isServer) {
					 ((Server) master).appendPacket(wrappedPacket);
				}
				else {
					 ((Client) master).appendPacket(wrappedPacket);

				}
//				 System.out.println("Workter Thread Appended Message");
			} catch (ClassCastException e) {
				// TODO Auto-generated catch block
				if (isServer) {
					 ((Server) master).appendPacket((WrappedPacket) Server.getObjectFromByte(packet.getData()));
				}
				else {
					 ((Client) master).appendPacket((WrappedPacket) Server.getObjectFromByte(packet.getData()));

				}
				continue;
			} catch (InterruptedException e) {
				continue;
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				continue;
			}
		}
	}

}
