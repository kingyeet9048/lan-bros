package cs410.lanbros.network.packets;

import java.io.Serializable;


public class PlayerInputPacket implements Packet<InputTypes>, Serializable{

	// instance variables
	private static final long serialVersionUID = -4824990435550206712L;
	private InputTypes inputTypes;
	private String packetReceiver;
	private String packetSender;
	private String playerMoving;
	
	public PlayerInputPacket(String packetReceiver, String packetSender) {
		this.packetReceiver = packetReceiver;
		this.packetSender = packetSender;
	}

	public InputTypes getInputTypes() {
		return inputTypes;
	}

	public void setInputTypes(InputTypes inputTypes) {
		this.inputTypes = inputTypes;
	}

	@Override
	public InputTypes getData() {
		// TODO Auto-generated method stub
		return inputTypes;
	}

	@Override
	public boolean isServerPacket() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPacketReceiver() {
		// TODO Auto-generated method stub
		return packetReceiver;
	}

	@Override
	public void setPacketReceiver(String packetReceiver) {
		// TODO Auto-generated method stub
		this.packetReceiver = packetReceiver;
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

	public String getPlayerMoving() {
		return playerMoving;
	}

	public void setPlayerMoving(String playerMoving) {
		this.playerMoving = playerMoving;
	}

}
