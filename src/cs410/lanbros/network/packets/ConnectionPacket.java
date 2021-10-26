package cs410.lanbros.network.packets;

import java.io.Serializable;

public class ConnectionPacket implements Packet<Boolean>, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2408776396577264333L;
	private String packetReceiver;
	private String packetSender;
	private boolean isConnected;
	private String playerName;
	
	public ConnectionPacket(String packetReceiver, String packetSender) {
		// TODO Auto-generated constructor stub
		this.packetReceiver = packetReceiver;
		this.packetSender = packetSender;
	}

	@Override
	public Boolean getData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isServerPacket() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setPacketReceiver(String packetReceiver) {
		// TODO Auto-generated method stub
		this.packetReceiver = packetReceiver;
	}

	@Override
	public String getPacketReceiver() {
		// TODO Auto-generated method stub
		return packetReceiver;
	}

	@Override
	public void setPacketSender(String packetSender) {
		// TODO Auto-generated method stub
		this.packetSender = packetSender;
	}

	@Override
	public String getPacketSender() {
		// TODO Auto-generated method stub
		return packetSender;
	}

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	

}
