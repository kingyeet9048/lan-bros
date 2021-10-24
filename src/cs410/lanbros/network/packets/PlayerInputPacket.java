package cs410.lanbros.network.packets;

import java.io.Serializable;

public class PlayerInputPacket implements Packet<InputTypes>, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4824990435550206712L;
	private InputTypes inputTypes;
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

}
