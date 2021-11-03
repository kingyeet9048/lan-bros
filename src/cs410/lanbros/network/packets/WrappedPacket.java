package cs410.lanbros.network.packets;

import java.io.Serializable;

public class WrappedPacket implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8119757909524435185L;
	private PacketType packetType;
	private Serializable packet;
	
	public WrappedPacket(Serializable packet, PacketType packetType) {
		// TODO Auto-generated constructor stub
		this.packet = packet;
		this.packetType = packetType;
	}
	public PacketType getPacketType() {
		return packetType;
	}
	public void setPacketType(PacketType packetType) {
		this.packetType = packetType;
	}
	public Serializable getPacket() {
		return packet;
	}
	public void setPacket(Serializable packet) {
		this.packet = packet;
	}
}
